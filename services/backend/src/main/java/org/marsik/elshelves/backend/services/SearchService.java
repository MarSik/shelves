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
import org.apache.commons.lang.text.StrTokenizer;

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

        Set<NamedEntity> result = null;
        if (q.length() > 2) {
            for (String partialQuery: new StrTokenizer(q, ' ', '"').getTokenArray()) {
                Set<NamedEntity> partialResult = new THashSet<>();

                for (NamedEntity e : entityRepository.queryByUser(currentUser, "(?i).*" + partialQuery + ".*")) {
                    partialResult.add(e);
                }

                if (result == null) {
                    result = partialResult;
                } else {
                    result.retainAll(partialResult);
                }
            }
        }

        searchResult.setItems(new THashSet<PolymorphicRecord>());
        if (result == null) {
            return searchResult;
        }

        for (NamedEntity n: result) {
            PolymorphicRecord record = new PolymorphicRecord();
            record.setId(n.getUuid());
            record.setType(n.getEmberType());
            searchResult.getItems().add(record);
        }

        return searchResult;
    }
}
