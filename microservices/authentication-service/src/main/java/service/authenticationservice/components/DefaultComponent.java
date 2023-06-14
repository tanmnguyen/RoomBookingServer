package service.authenticationservice.components;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;

@Log
@Component
public class DefaultComponent implements ApplicationRunner {

    private UserRepository userRepository;

    @Autowired
    public DefaultComponent(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String password = "pass";
        userRepository.save(new User("admin", password, "ADMIN"));
        userRepository.save(new User("malicious admin", password, "ADMIN"));
        userRepository.save(new User("malicious employee", password, "EMPLOYEE"));
        userRepository.save(new User("malicious secretary", password, "SECRETARY"));
    }
}
