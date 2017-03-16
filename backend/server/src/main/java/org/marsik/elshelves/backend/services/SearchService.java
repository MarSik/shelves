package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.SearchResult;
import org.marsik.elshelves.backend.entities.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface SearchService {
    SearchResult query(String q, User currentUser, Map<UUID, Object> cache, Set<String> include, String type, int limit);
}
