package service.authenticationservice.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;


@Log
@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by the given netId.

     * @param netId NetId of the user

     * @return the user information after get validation from spring security.
     * @throws UsernameNotFoundException when the provided netId is not in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String netId) throws UsernameNotFoundException {
        Optional<User> users = userRepository.findUserByNetIdEquals(netId);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException(netId + " does not match any user");
        }

        User user = users.get();

        Collection<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
        authorityCollection.add(new SimpleGrantedAuthority(user.getRole().toString()));

        return new org.springframework.security.core
                .userdetails.User(user.getNetId(), user.getPassword(), authorityCollection);
    }
}
