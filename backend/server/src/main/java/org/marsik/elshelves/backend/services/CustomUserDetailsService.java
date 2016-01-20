package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.security.SimpleAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements ElshelvesUserDetailsService {
    private static final long serialVersionUID = 1L;

    @Autowired
    transient UserRepository userRepository;

    @Autowired
    transient AuthorizationRepository authorizationRepository;

    @Autowired
    transient PasswordEncoder passwordEncoder;

    @Autowired
    transient UuidGenerator uuidGenerator;

    @Autowired
    transient EmberToUser emberToUser;

	@Autowired
	transient UserToEmber userToEmber;

    @PersistenceContext
    private EntityManager entityManager;

    @CircuitBreaker
    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    @CircuitBreaker("loadUserByUsername")
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try fetching Authorization object with the email as ID
        // use it for logging in when it exists (mobile device logging in)
        try {
            Authorization auth = authorizationRepository.findById(UUID.fromString(email));
            if (auth != null) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleAuthority("ROLE_MOBILE"));
                authorities.add(new SimpleAuthority("ROLE_USER"));

                return new org.springframework.security.core.userdetails.User(
                        auth.getId().toString(),
                        auth.getSecret(),
                        authorities);
            }
        } catch (IllegalArgumentException ex) {
            // ignore non UUID logins here
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
        authorities.add(new SimpleAuthority("ROLE_USER"));
        authorities.add(new SimpleAuthority("ROLE_IDENTITY"));

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                authorities);
    }

    @Override
    @Transactional
    @CircuitBreaker("createUser")
    public String createUser(UserApiModel userInfo) throws OperationNotPermitted {
		if (userRepository.getUserByEmail(userInfo.getEmail()) != null) {
			throw new OperationNotPermitted();
		}

        User user = emberToUser.convert(userInfo, new THashMap<UUID, Object>());
        user.setId(uuidGenerator.generate());
		user.setVerificationCode(RandomStringUtils.randomAlphanumeric(20));
		user.setVerificationStartTime(new DateTime());
		user.setRegistrationDate(new DateTime());
        user.setCreated(new DateTime());
        user.setLastModified(user.getCreated());

        userRepository.save(user);

        return user.getVerificationCode();
    }

    @Override
    @Transactional
    public User createOrAttachUser(String email, String externalId) {
        User user = userRepository.getUserByExternalIds(externalId);

        if (user != null) {
            return user;
        } else if (email != null) {
            user = userRepository.getUserByEmail(email);
        }

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setId(uuidGenerator.generate());
            user.setRegistrationDate(new DateTime());
            user.setCreated(new DateTime());
        }

        user.getExternalIds().add(externalId);
        user.setLastModified(user.getCreated());

        user = userRepository.save(user);
        userRepository.flush();
        entityManager.detach(user);
        return user;
    }

    @Override
    @Transactional
    public User attachUser(User user, String externalId) {
        User realUser = userRepository.findById(user.getId());
        realUser.getExternalIds().add(externalId);
        realUser.setLastModified(new DateTime());
        userRepository.save(realUser);
        userRepository.flush();
        entityManager.detach(realUser);
        return realUser;
    }

    @Override
    @CircuitBreaker("startUserVerification")
	public String startNewVerification(String email) {
		User u = userRepository.getUserByEmail(email);
		if (u == null) {
			return null;
		}

		DateTime timeLimit = new DateTime();
		timeLimit.minusMinutes(15);

		/* One verification per 15 minutes */
		if (u.getVerificationStartTime() != null
				&& u.getVerificationStartTime().isAfter(timeLimit)) {
			return null;
		}

		u.setVerificationCode(RandomStringUtils.randomAlphanumeric(20));
		u.setVerificationStartTime(new DateTime());
		return u.getVerificationCode();
	}

    @Override
    @CircuitBreaker("verifyUser")
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

		UserApiModel response = userToEmber.convert(u, new THashMap<UUID, Object>());
		response.setPassword(password);

        return response;
    }
}
