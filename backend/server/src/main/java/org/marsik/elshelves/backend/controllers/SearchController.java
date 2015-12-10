package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
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
        SearchResult result = searchService.query(query.getQuery(), currentUser, cache, query.getInclude());
        EmberModel.Builder<SearchResult> searchResultBuilder = new EmberModel.Builder<SearchResult>(result);

        for (AbstractEntityApiModel item: result.getItems()) {
            cache.remove(item.getId());
        }

        searchResultBuilder.sideLoad(cache.values());

        return searchResultBuilder.build();
    }
}
