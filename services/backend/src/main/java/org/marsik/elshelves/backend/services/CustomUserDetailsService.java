package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements ElshelvesUserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorizationRepository authorizationRepository;

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
        // Try fetching Authorization object with the email as ID
        // use it for logging in when it exists (mobile device logging in)
        try {
            Authorization auth = authorizationRepository.findByUuid(UUID.fromString(email));
            if (auth != null) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return "REMOTE";
                    }
                });

                return new org.springframework.security.core.userdetails.User(
                        auth.getOwner().getEmail(),
                        auth.getSecret(),
                        authorities);
            }
        } catch (IllegalArgumentException ex) {
            // ignore
        }

        // Use the standard user/password mechanism for logging in
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
		user.setVerificationStartTime(new Date());
		user.setRegistrationDate(new Date());

        userRepository.save(user);

        return user.getVerificationCode();
    }

	@Override
	public String startNewVerification(String email) {
		User u = userRepository.getUserByEmail(email);
		if (u == null) {
			return null;
		}

		Calendar timeLimit = new GregorianCalendar();
		timeLimit.add(Calendar.MINUTE, -15);

		/* One verification per 15 minutes */
		if (u.getVerificationStartTime() != null
				&& u.getVerificationStartTime().after(timeLimit.getTime())) {
			return null;
		}

		u.setVerificationCode(RandomStringUtils.randomAlphanumeric(20));
		u.setVerificationStartTime(new Date());
		return u.getVerificationCode();
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
		u.setVerificationStartTime(null);
        userRepository.save(u);

		UserApiModel response = userToEmber.convert(u, 1, new THashMap<UUID, Object>());
		response.setPassword(password);

        return response;
    }
}
