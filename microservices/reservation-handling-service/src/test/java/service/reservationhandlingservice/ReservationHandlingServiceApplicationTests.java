package service.reservationhandlingservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import service.reservationhandlingservice.repositories.ReservationRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationHandlingServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    void contextLoads() {
    }

    //@Test
    //public void main() {
    //ReservationHandlingServiceApplication.main(new String[] {});
    //}

}
