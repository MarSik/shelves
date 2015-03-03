package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.SanityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class SanityService {
    private static final Logger logger = LoggerFactory.getLogger(SanityService.class);

    @Autowired
    SanityRepository sanityRepository;

    @Autowired
    Neo4jTemplate neo4jTemplate;

    @Autowired
    UuidGenerator uuidGenerator;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void collectOrphanedTypes() {
        Iterable<Type> types = sanityRepository.findOrphanedTypes();
        Map<User, Group> orphanGroups = new THashMap<>();

        String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(new Date());

        for (Type t: types) {
            User u = t.getOwner();
            if (u == null) {
                logger.warn("Type {} does not belong to anybody!", t);
                continue;
            }

            if (!orphanGroups.containsKey(u)) {
                Group group = new Group();
                group.setUuid(uuidGenerator.generate());
                group.setOwner(u);
                group.setName("Orphaned types - " + date);
                orphanGroups.put(u, group);
            }

            Group g = orphanGroups.get(u);
            logger.info("Adding type {} to orphan group {} for user {}", t, g, u);
            t.getGroups().add(g);
            neo4jTemplate.save(t);
        }
    }
}
