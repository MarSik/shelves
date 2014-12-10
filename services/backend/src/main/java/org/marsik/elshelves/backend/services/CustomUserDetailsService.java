package org.marsik.elshelves.backend.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.getUserByEmail(email);

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

    public String createUser(org.marsik.elshelves.api.entities.User userInfo) {
        User user = User.fromDto(userInfo);
        user.setId(null);
        user.setPassword(null);

        userRepository.save(user);

        return user.getVerificationCode();
    }

    public String verifyUser(String code) {
        User u = userRepository.getUserByVerificationCode(code);

        String password = RandomStringUtils.randomAlphanumeric(10);
        u.setPassword(passwordEncoder.encode(password));
        u.setVerificationCode(null);
        userRepository.save(u);

        return password;
    }
}
