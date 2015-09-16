package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang3.text.StrTokenizer;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Set<NamedEntity> result = null;
        if (q.length() > 2) {
            for (String partialQuery: new StrTokenizer(q, ' ', '"').getTokenArray()) {
                Set<NamedEntity> partialResult = new THashSet<>();

                for (NamedEntity e : entityRepository.queryByOwnerAndText(
                        currentUser,
                        "%" + partialQuery.toLowerCase() + "%")) {
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
