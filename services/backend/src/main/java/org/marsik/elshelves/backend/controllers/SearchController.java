package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
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


@RequestMapping("/v1/searches")
@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.POST)
    public EmberModel search(@CurrentUser User currentUser,
                             @RequestBody SearchResult query) {
        SearchResult result = searchService.query(query.getQuery(), currentUser);
        EmberModel.Builder<SearchResult> searchResultBuilder = new EmberModel.Builder<SearchResult>(result);
        return searchResultBuilder.build();
    }
}
