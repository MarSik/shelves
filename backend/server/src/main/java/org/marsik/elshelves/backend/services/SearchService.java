package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang3.text.StrTokenizer;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SearchService {
    private final static Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    NamedEntityRepository entityRepository;

    @Autowired
    EntityToEmberConversionService conversionService;

    public SearchResult query(String q, User currentUser, Map<UUID, Object> cache, Set<String> include) {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(uuidGenerator.generate());
        searchResult.setQuery(q);
        searchResult.setInclude(include);

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

        searchResult.setItems(new THashSet<>());
        if (result == null) {
            return searchResult;
        }

        for (NamedEntity n: result) {
            final CachingConverter<NamedEntity, AbstractNamedEntityApiModel, UUID> converter = conversionService.converter(n, AbstractNamedEntityApiModel.class);
            if (converter == null) {
                logger.error("Could not find converter for {}.", n.getClass().getName());
                continue;
            }
            AbstractNamedEntityApiModel record = converter
                    .convert(null, "item", n, cache, searchResult.getInclude());
            searchResult.getItems().add(record);
        }

        searchResult.setEntityType(searchResult.getEmberType());
        return searchResult;
    }
}
