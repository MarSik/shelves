package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequestMapping("/v1/searches")
@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.POST)
    public EmberModel search(@CurrentUser User currentUser,
                             @RequestBody SearchResult query) {
        Map<UUID, Object> cache = new THashMap<>();
        SearchResult result = searchService.query(query.getQuery(), currentUser, cache,
                query.getInclude(), query.getType(), query.getTotal());
        EmberModel.Builder<SearchResult> searchResultBuilder = new EmberModel.Builder<SearchResult>(result);
        cache.remove(result);
        searchResultBuilder.sideLoad(cache.values());

        return searchResultBuilder.build();
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.GET)
    public EmberModel search(@CurrentUser User currentUser,
                                     @RequestParam("q") String query,
                                     @RequestParam(value = "c", required = false) Integer maximum,
                                     @RequestParam(value = "i", required = false) String include,
                                     @RequestParam(value = "t", required = false) String type) {
        Map<UUID, Object> cache = new THashMap<>();
        SearchResult result = searchService.query(query, currentUser, cache,
                Optional.ofNullable(include)
                        .map(s -> s.split(";"))
                        .map(SearchController::asSet)
                        .orElseGet(Collections::emptySet),
                type,
                Optional.ofNullable(maximum).orElse(0));
        EmberModel.Builder<SearchResult> searchResultBuilder = new EmberModel.Builder<SearchResult>(result);
        cache.remove(result);
        searchResultBuilder.sideLoad(cache.values());

        return searchResultBuilder.build();
    }

    private static Set<String> asSet(String... list) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(list)));
    }
}
