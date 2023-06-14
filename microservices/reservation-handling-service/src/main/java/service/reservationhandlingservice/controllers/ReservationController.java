package service.reservationhandlingservice.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.reservationhandlingservice.authentications.AuthenticateRequest;
import service.reservationhandlingservice.entities.Reservation;
import service.reservationhandlingservice.entities.ReservationHolder;
import service.reservationhandlingservice.repositories.ResearchGroupRepository;
import service.reservationhandlingservice.repositories.ReservationRepository;


@Log
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ResearchGroupRepository researchGroupRepository;

    /**Description: Initialise the Reservation controller.
     *
     * @param reservationRepository The reservation repository that keeps all the reservations.
     * @param researchGroupRepository Research group repository that keeps all the research groups.
     */
    @Autowired
    public ReservationController(ReservationRepository reservationRepository,
                                 ResearchGroupRepository researchGroupRepository) {
        this.reservationRepository = reservationRepository;
        this.researchGroupRepository = researchGroupRepository;
        //Only For test purposes================================================================
        //reservationRepository.save(new Reservation("admin", 4L, new Date()));
        //reservationRepository.save(new Reservation("admin", 7L, new Date()));
        //reservationRepository.save(new Reservation("secretary", 4L, new Date()));
        //reservationRepository.save(new Reservation("john", 5L, new Date()));
        //======================================================================================

    }

    /**
     * Cancel the reservation.
     * Only the reservation created by the user who is requesting can be cancelled.
     *
     * @param request       Http request.
     * @param reservationId Id of the reservation.
     * @return A boolean value indicating if the cancellation is successful.
     */
    @DeleteMapping("/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelReservation(HttpServletRequest request,
                                               @RequestBody long reservationId) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String netId = AuthenticateRequest.getAuthenticationResponse().getNetId();
        String role = AuthenticateRequest.getAuthenticationResponse().getRole();

        Optional<Reservation> reservationOptional;

        // Get reservations from the database
        if (role.equals("admin")) {
            reservationOptional = reservationRepository.findById(reservationId);
        } else {
            reservationOptional = reservationRepository
                .findByHostIdAndReservationId(netId, reservationId);
        }

        // Prevent user from canceling reservation that does not exist or belongs to another user.
        if (reservationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Reservation reservation = reservationOptional.get();
        reservationRepository.delete(reservation);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        log.info("endpoint reached: hello world is being screamed out!");
        return ResponseEntity.ok("hello");
    }


    /**Description: This endpoint is to list all the reservation from a user
     * and return them to him ina readable way.
     *
     * @param request The request from the user containing the validate token.
     * @return A user readable list of his own reservations.
     * @throws Exception if the user doesn't have a valid token, the authenticationResponse
     *         will throw an exception
     */

    @GetMapping("/myReservations")
    public ResponseEntity<?> listReservations(HttpServletRequest request)  throws Exception {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String netId = AuthenticateRequest.getAuthenticationResponse().getNetId();

        // Get reservations from the database
        Optional<List<Reservation>> reservations = reservationRepository.findAllByHostId(netId);

        // Prevent user from canceling reservation that is not exist or belongs to another user.
        // For now admin option has not been added.
        if (reservations.isEmpty()) {
            return ResponseEntity.ok("No reservations found");
        }

        Stream<Reservation> stream = reservations.orElse(Collections.emptyList()).stream();

        final String[] output = {"All your reservations:\n"};
        stream.forEach(reservation ->  output[0] += reservation.toString() + "\n");


        return ResponseEntity.ok(output[0]);
    }

    /**
     * Create a reservation.
     * Reservation can only be created by auth
     *
     * @param request       Http request.
     * @param holder The object holding the startTime, endTime and the roomId
     * @throws Exception if the user doesn't have a valid token, the authenticationResponse
     *                  will throw an exception
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReservation(HttpServletRequest request,
                                               @RequestBody ReservationHolder holder)
        throws Exception {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        //Create 2 formats to parse the input strings into LocalDateTime objects
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime begin;
        LocalDateTime end;

        try {
            begin = LocalDateTime.parse(holder.getStartTime(), formatter1);
            end = LocalDateTime.parse(holder.getEndTime(), formatter1);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please enter a date in the format : yyyy-MM-dd HH:mm");
        }

        //Get the dates of today and two weeks from today to perform checks
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime twoWeeks = LocalDateTime.now().plusWeeks(2);

        String netId = AuthenticateRequest.getAuthenticationResponse().getNetId();

        //Prevent users from reserving dates before the current day, after 2 weeks from today,
        // on different days and from reserving an end time that is before the start time
        if (begin.isBefore(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("You can not create reservations before today");
        } else if (begin.isAfter(twoWeeks)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("You can only create reservations 2 weeks in advance");
        } else if (begin.isAfter(end)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The end time can not be before the start time");
        } else if (!formatter2.format(begin).equals(formatter2.format(end))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The end time should be on the same day as the start time");
        }

        //Checks if the reservation is inside the building hours and the timeslot is not overlapping with another
        //reservation in the same room
        if (!checkReservation(new Reservation(netId, holder.getRoomId(), begin, end))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations");
        }

        //Create and save the new reservation in the repository, then send an ok response
        Reservation newReservation = new Reservation(netId, holder.getRoomId(), begin, end);
        reservationRepository.save(newReservation);
        return ResponseEntity.status((HttpStatus.OK)).body("Created the reservation succesfully");
    }

    /**
     * Method to check if the reservation is available.
     *
     * @param reservation the reservation in question
     * @return boolean true if the reservation is valid, false if the reservation is outside the building's opening hours
     *      or if the reservation overlaps with another reservation in the same building
     */
    public boolean checkReservation(Reservation reservation) {
        //If there are overlapping reservations, return false, if there arent, keep going
        if (overlappingReservations(reservation)) {
            return false;
        }

        //Split the buildingAndRoomId into just the building id and parse that to a long
        String[] strValues = String.valueOf(reservation.getRoomId()).split("\\.");
        Long building = Long.parseLong(strValues[0]);
        LocalTime[] times;
        //Call a method that returns the opening hours of a building
        try {
            times = getOpeningHours(building);
        } catch (Exception e) {
            return false;
        }

        //If the reservation times are outside of the building hours, return false
        LocalTime startTime = reservation.getStartTime().toLocalTime();
        LocalTime endTime = reservation.getEndTime().toLocalTime();
        return !startTime.isBefore(times[0]) && !endTime.isAfter(times[1]);
    }

    /**
     * Method that returns the opening hours of a building.
     *
     * @param buildingId Id of the building to be reserved.
     * @return an array with the opening and closing time of the building
     * @throws Exception if the communication with the buildingandrooms microservice fails
     */
    public LocalTime[] getOpeningHours(long buildingId) throws Exception {
        String openingHoursPath = "http://localhost:8082/buildingandrooms/getOpeningHours?buildingId=" + buildingId;
        URI openingHoursUri = URI.create(openingHoursPath);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(openingHoursUri);

        // Create request.
        HttpRequest httpRequest = builder.build();

        // Get response
        HttpResponse<String> response;
        try {
            response = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("Error while communicating with authentication server", e);
        }

        int okCode = 200;
        // Status code != successful
        if (response.statusCode() != okCode) {
            throw new Exception("Error - Status code = " + response.statusCode());
        }

        // Response is received from an undesired address.
        if (!response.uri().equals(openingHoursUri)) {
            throw new Exception("Response from unknown address");
        }

        String[] timesString = response.body().split(", ");
        LocalTime start;
        LocalTime end;

        try {
            start = LocalTime.parse(timesString[0]);
            end = LocalTime.parse(timesString[1]);
        } catch (DateTimeParseException e) {
            throw new Exception("Invalid start and/or end times");
        }
        LocalTime[] times = new LocalTime[2];
        times[0] = start;
        times[1] = end;
        return times;
    }

    /**
     * Method to check if the reservation is overlapping with other reservations in the same room.
     *
     * @param reservation the reservation in question
     * @return boolean true if the reservation is overlapping with another reservation, false if not.
     */
    public boolean overlappingReservations(Reservation reservation) {
        Optional<List<Reservation>> optionalReservations = reservationRepository.findAllByRoomId(reservation.getRoomId());
        Optional<List<Reservation>> optionalUserReservations = reservationRepository.findAllByHostId(reservation.getHostId());

        if (optionalReservations.isEmpty() & optionalUserReservations.isEmpty()) {
            return false;
        }

        List<Reservation> reservations = Stream.of(optionalReservations, optionalUserReservations)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();
        LocalDate date = start.toLocalDate();
        for (Reservation r : reservations) {
            LocalDate reservedDate = r.getStartTime().toLocalDate();
            if (date.isEqual(reservedDate)) {
                //Get the start and end times of the new reservation and the start and end time in the
                //already reserved reservations.
                LocalTime startTime = start.toLocalTime();
                LocalTime endTime = end.toLocalTime();
                LocalTime rstartTime = r.getStartTime().toLocalTime();
                LocalTime rendTime = r.getEndTime().toLocalTime();
                if (startTime.isAfter(rstartTime) & startTime.isBefore(rendTime)) {
                    return true;
                }
                if (startTime.isBefore(rstartTime) && endTime.isAfter(rstartTime)) {
                    return true;
                }
                if (startTime.equals(rstartTime)) {
                    return true;
                }
                if (endTime.equals(rendTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Edit the reservation.
     * Only the reservation created by the user who is requesting can be edited.
     * The reservation can only be edited to another available time-slot/room
     *
     * @param request       Http request.
     * @param reservationId Id of the reservation.
     * @return A boolean value indicating if the cancellation is successful.
     */
    @PutMapping("/edit/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> editReservation(HttpServletRequest request,
                                             @RequestParam long reservationId,
                                             @RequestParam String roomId,
                                             @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime finishTime) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String netId = AuthenticateRequest.getAuthenticationResponse().getNetId();
        String role = AuthenticateRequest.getAuthenticationResponse().getRole();

        Optional<Reservation> reservationOptional;

        // Issue: Secretary able to edit reservation made by her research group members
        boolean hasMembers = true;
        if (role.equals("secretary")) {

            Optional<List<String>> users = researchGroupRepository.findAllBySecretaryId(netId);
            if (users.isEmpty()) {
                hasMembers = false;
            }
            if (hasMembers) {
                List<String> groupMembers = new ArrayList<>();
                groupMembers.addAll(users.get());

                List<Reservation> allReservations = new ArrayList<>();
                for (String member : groupMembers) {
                    Optional<List<Reservation>> reservations = reservationRepository.findAllByHostId(member);
                    if (reservations.isEmpty()) {
                        continue;
                    }
                    allReservations.addAll(reservations.get());
                }

                for (Reservation reservation : allReservations) {
                    if (reservation.getReservationId() == reservationId) {

                        //create new reservation with updated time and place
                        Reservation newReservation = new Reservation(reservation.getHostId(), roomId, startTime, finishTime);

                        //check if a reservation already exists in that room at that time
                        Optional<Reservation> previousReservation = reservationRepository
                            .findByRoomIdAndStartTimeAndEndTime(roomId, startTime, finishTime);

                        //if there is no reservation, proceed with editing the reservation
                        if (previousReservation.isEmpty()) {
                            reservationRepository.save(newReservation);
                            reservationRepository.delete(reservation);
                        } else { //if there already is a reservation, edit not possible
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }

                        return ResponseEntity.ok("Edit successful");
                    }
                }
            }
        }

        // Get reservations from the database
        if (role.equals("admin")) {
            reservationOptional = reservationRepository.findById(reservationId);
        } else {
            reservationOptional = reservationRepository
                .findByHostIdAndReservationId(netId, reservationId);
        }

        // Prevent user from canceling reservation that is not exist or belongs to another user if the user is not an admin.
        if (reservationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //get initial reservation
        Reservation reservation = reservationOptional.get();

        //create new reservation with updated time and place
        Reservation newReservation = new Reservation(reservation.getHostId(), roomId, startTime, finishTime);

        //check if a reservation already exists in that room at that time
        Optional<Reservation> previousReservation = reservationRepository
            .findByRoomIdAndStartTimeAndEndTime(roomId, startTime, finishTime);

        //if there is no reservation, proceed with editing the reservation
        if (previousReservation.isEmpty()) {
            reservationRepository.save(newReservation);
            reservationRepository.delete(reservation);
        } else { //if there already is a reservation, edit not possible
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok("Edit successful");
    }

}
