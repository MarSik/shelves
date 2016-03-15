package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang3.text.StrTokenizer;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.NamedEntity_;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SearchServiceImpl implements SearchService {
    private final static Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    NamedEntityRepository entityRepository;

    @Autowired
    EntityToEmberConversionService conversionService;

    public static Specification<NamedEntity> searchSpecification(User user, String... queries) {
        return new Specification<NamedEntity>() {
            @Override
            public Predicate toPredicate(Root<NamedEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(criteriaBuilder.equal(root.get(NamedEntity_.owner), user));

                for (String q: queries) {
                    q = "%" + q.toLowerCase() + "%";
                    List<Predicate> orPredicates = new ArrayList<>();
                    orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(NamedEntity_.name)), q));
                    orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(NamedEntity_.description)), q));
                    orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(NamedEntity_.summary)), q));
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    @Override
    public SearchResult query(String q, User currentUser, Map<UUID, Object> cache, Set<String> include) {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(uuidGenerator.generate());
        searchResult.setQuery(q);
        searchResult.setInclude(include);

        final String[] tokenArray = new StrTokenizer(q, ' ', '"').getTokenArray();
        Pageable pageable = new PageRequest(0, 100);
        Page<NamedEntity> result = entityRepository.findAll(searchSpecification(currentUser, tokenArray), pageable);

        searchResult.setItems(new THashSet<>());
        if (result == null) {
            return searchResult;
        }

        searchResult.setTotal(result.getTotalElements());

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
