package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class SearchService {
    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    NamedEntityRepository entityRepository;

    public SearchResult query(String q, User currentUser) {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(uuidGenerator.generate());
        searchResult.setQuery(q);

        Set<PolymorphicRecord> result = new THashSet<>();
        if (q.length() > 2) {
            for (NamedEntity e : entityRepository.queryByUser(currentUser, "(?i).*" + q + ".*")) {
                PolymorphicRecord record = new PolymorphicRecord();
                record.setId(e.getUuid());
                record.setType(e.getEmberType());
                result.add(record);
            }
        }

        searchResult.setItems(result);
        return searchResult;
    }
}
