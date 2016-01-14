package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.SanityRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanityService {
    private static final Logger logger = LoggerFactory.getLogger(SanityService.class);

    @Autowired
    SanityRepository sanityRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    UuidGenerator uuidGenerator;

    @Transactional
    @CircuitBreaker
    @Scheduled(cron = "0 */5 * * * *")
    public void collectOrphanedTypes() {
        Iterable<Type> types = sanityRepository.findOrphanedTypes();

        for (Type t: types) {
            User u = t.getOwner();
            if (u == null) {
                logger.warn("Type {} does not belong to anybody!", t);
                continue;
            }

            if (u.getLostAndFound() == null) {
                Group group = new Group();
                group.setId(uuidGenerator.generate());
                group.setOwner(u);
                group.setName("Lost and found");

                u.setLostAndFound(group);

                try {
                    groupService.create(group, u);
                    userRepository.save(u);
                } catch (OperationNotPermitted operationNotPermitted) {
                    operationNotPermitted.printStackTrace();
                }
            }

            Group g = u.getLostAndFound();
            logger.info("Adding type {} to orphan group {} for user {}", t, g, u);
            t.addGroup(g);
            typeRepository.save(t);
        }
    }
}
