package org.marsik.elshelves.backend.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements ElshelvesUserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UuidGenerator uuidGenerator;

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exist.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "USER";
            }
        });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public String createUser(org.marsik.elshelves.api.entities.User userInfo) {
        User user = User.fromDto(userInfo);
        user.setUuid(uuidGenerator.generate());
        user.setPassword(null);

        userRepository.save(user);

        return user.getVerificationCode();
    }

    @Override
    public String verifyUser(String code) {
        User u = userRepository.getUserByVerificationCode(code);

        String password = RandomStringUtils.randomAlphanumeric(10);
        u.setPassword(passwordEncoder.encode(password));
        u.setVerificationCode(null);
        userRepository.save(u);

        return password;
    }
}
