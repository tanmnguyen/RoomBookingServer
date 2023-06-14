package service.buildingandroomhandlingservice.components;


import java.util.ArrayList;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.repositories.RoomRepository;

// This class is for testing only
// TODO: remove this component

@Log
@Component
public class DefaultComponent implements ApplicationRunner {

    private RoomRepository roomRepository;

    @Autowired
    public DefaultComponent(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // default room
        roomRepository.save(new Room(1,
            1, 10,
            new ArrayList<Equipment>()));
    }
}
