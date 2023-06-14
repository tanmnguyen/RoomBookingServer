package service.authenticationservice.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    public enum Role {
        EMPLOYEE,
        SECRETARY,
        ADMIN
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "auth_account_id")
    private Long id;

    @Column(name = "net_id", nullable = false)
    private String netId;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255) default 'EMPLOYEE'")
    private Role role;


    public User(){}

    /**
     * Constructs a new User.

     * @param netId - netId of the user

     * @param password - password in string format
     */
    public User(String netId, String password, String role) {
        this.netId = netId;
        this.password = password;
        this.role = Role.valueOf(role);
    }

    /**
     * Constructs a new User.

     * @param netId - netId of the user
     * @param password - password in string format
     */
    public User(String netId, String password, Role role) {
        this.netId = netId;
        this.password = password;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }
}