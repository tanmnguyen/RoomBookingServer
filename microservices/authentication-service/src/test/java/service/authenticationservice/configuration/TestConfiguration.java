package service.authenticationservice.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import service.authenticationservice.security.Configuration;

public class TestConfiguration {

    /**
     * Kill mutation test: the password encode must not return null.
     */
    @Test
    void testPasswordEncoderNotNull() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration.passwordEncoder());

    }

    /**
     * Kill mutation test: the authenticationManager must not return null.
     * Expecting the authenticationManagerBean function to throw exception when no configuration is initialized.
     */
    @Test
    void testAuthenticationManagerBeaNotNull() {
        Configuration configuration = new Configuration();

        assertThrows(Exception.class, configuration::authenticationManagerBean);
    }
}

