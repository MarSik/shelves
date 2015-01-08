package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
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
import java.util.UUID;

@Service
public class CustomUserDetailsService implements ElshelvesUserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    EmberToUser emberToUser;

	@Autowired
	UserToEmber userToEmber;

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

		if (user.getPassword() == null) {
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
    public String createUser(UserApiModel userInfo) throws OperationNotPermitted {
		if (userRepository.getUserByEmail(userInfo.getEmail()) != null) {
			throw new OperationNotPermitted();
		}

        User user = emberToUser.convert(userInfo, 1, new THashMap<UUID, Object>());
        user.setUuid(uuidGenerator.generate());
		user.setVerificationCode(RandomStringUtils.randomAlphanumeric(20));

        userRepository.save(user);

        return user.getVerificationCode();
    }

    @Override
    public UserApiModel verifyUser(String code) throws PermissionDenied {
        User u = userRepository.getUserByVerificationCode(code);

		if (u == null) {
			throw new PermissionDenied();
		}

        String password = RandomStringUtils.randomAlphanumeric(10);
        u.setPassword(passwordEncoder.encode(password));
        u.setVerificationCode(null);
        userRepository.save(u);

		UserApiModel response = userToEmber.convert(u, 1, new THashMap<UUID, Object>());
		response.setPassword(password);

        return response;
    }
}
