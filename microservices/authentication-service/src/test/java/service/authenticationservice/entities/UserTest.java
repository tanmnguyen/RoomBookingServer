package service.authenticationservice.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UserTest {
    /**
     * Test the empty constructor.
     */
    @Test
    void emptyConstructorTest() {
        User user = new User();
        assertNotNull(user);
    }

    /**
     * Test non-empty constructor.
     */
    @Test
    void constructorTest() {
        User userAdmin = new User("admin", "pass", "ADMIN");
        User userEmployee = new User("employee", "pass", "EMPLOYEE");
        User userSecretary = new User("secretary", "pass", "SECRETARY");

        assertNotNull(userAdmin);
        assertNotNull(userEmployee);
        assertNotNull(userSecretary);

        assertThrows(Exception.class, () -> {
            User userInvalid = new User("name", "pass", "random role");
        });
    }

    /**
     * Test non-empty constructor.
     */
    @Test
    void constructorTestWithEnumForRole() {
        User userAdmin = new User("admin", "pass", User.Role.ADMIN);
        User userEmployee = new User("employee", "pass", User.Role.EMPLOYEE);
        User userSecretary = new User("secretary", "pass", User.Role.SECRETARY);

        assertNotNull(userAdmin);
        assertNotNull(userEmployee);
        assertNotNull(userSecretary);
    }

    /**
     * Test get methods.
     */
    @Test
    void getTest() {
        User user = new User("admin", "pass", "ADMIN");
        assertEquals(user.getNetId(), "admin");
        assertEquals(user.getPassword(), "pass");
        assertEquals(user.getRole().toString(), "ADMIN");
    }

    /**
     * Test se methods.
     */
    @Test
    void setTest() {
        User user = new User("admin", "pass", "ADMIN");
        user.setNetId("new id");
        user.setPassword("new password");
        user.setRole(User.Role.SECRETARY);

        assertEquals(user.getNetId(), "new id");
        assertEquals(user.getPassword(), "new password");
        assertEquals(user.getRole(), User.Role.SECRETARY);


    }
}
