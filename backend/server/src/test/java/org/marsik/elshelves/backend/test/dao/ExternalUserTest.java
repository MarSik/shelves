package org.marsik.elshelves.backend.test.dao;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class ExternalUserTest extends BaseDaoTest {
    @Autowired
    UserRepository userRepository;

    @Spy
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    @Test
    @Transactional
    @Rollback
    public void testExternalUserLookup() {
        UUID uuid = uuidGenerator.generate();
        User user = new User();
        user.setName("Test user");
        user.setEmail("user@test.com");
        user.setId(uuid);
        user.registerInExternal("external@test");
        user.registerInExternal("external@test2");
        user.setCreated(new DateTime());
        user.setLastModified(new DateTime());

        userRepository.save(user);

        UUID uuid2 = uuidGenerator.generate();
        User user2 = new User();
        user2.setName("Test user 2");
        user2.setEmail("user2@test.com");
        user2.setId(uuid2);
        user2.registerInExternal("external2@test");
        user2.setCreated(new DateTime());
        user2.setLastModified(new DateTime());

        userRepository.save(user2);
        userRepository.flush();

        User reported = userRepository.getUserByExternalIds("external@test");
        assertEquals(user, reported);

        reported = userRepository.getUserByExternalIds("external@test2");
        assertEquals(user, reported);

        reported = userRepository.getUserByExternalIds("external2@test");
        assertEquals(user2, reported);
    }
}
