package org.marsik.elshelves.backend.controllers;

import com.google.common.collect.Sets;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.LotHistoryToEmber;
import org.marsik.elshelves.backend.repositories.LotHistoryRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.ember.EmberModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

@RestController
@RequestMapping("/v1/history")
public class LotHistoryController {
    @Autowired
    LotHistoryRepository lotHistoryRepository;

    @Autowired
    LotHistoryToEmber lotHistoryToEmber;

    @RequestMapping("/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public EmberModel getOne(@CurrentUser User currentUser,
                             @PathVariable("id") UUID uuid) throws PermissionDenied, EntityNotFound {
        LotHistory entity = lotHistoryRepository.findById(uuid);
        LotHistoryApiModel dto = lotHistoryToEmber.convert(entity, new THashMap<>());

        EmberModel.Builder<LotHistoryApiModel> builder = new EmberModel.Builder<LotHistoryApiModel>(dto);
        return builder.build();
    }

    @RequestMapping
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<EmberModel> getAll(@CurrentUser User currentUser,
                                             @RequestParam(value = "ids[]", required = false) UUID[] ids,
                                             @RequestParam(value = "include", required = false) String include) throws BaseRestException {
        Collection<LotHistory> allItems;

        if (ids == null) {
            throw new IllegalArgumentException("List of IDs is required.");
        } else {
            allItems = new THashSet<>();
            for (UUID id: ids) {
                allItems.add(lotHistoryRepository.findById(id));
            }
        }

        Collection<LotHistoryApiModel> allDtos = new THashSet<>();
        Map<UUID, Object> cache = new THashMap<>();

        for (LotHistory entity: allItems) {
            allDtos.add(lotHistoryToEmber.convert(null, null, entity, cache, processInclude(include)));
        }

        EmberModel.Builder<LotHistoryApiModel> builder = new EmberModel.Builder<>(allDtos);

        for (LotHistoryApiModel entity: allDtos) {
            cache.remove(entity.getId());
        }

        builder.sideLoad(cache.values());

        // Empty list
        if (allItems.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .lastModified(new DateTime().getMillis())
                    .body(builder.build());
        }

        LotHistory lastModified = allItems.stream().reduce(new BinaryOperator<LotHistory>() {
            @Override
            public LotHistory apply(LotHistory t, LotHistory t2) {
                if (t.getLastModified().isAfter(t2.getLastModified())) {
                    return t;
                } else {
                    return t2;
                }
            }
        }).get();

        return ResponseEntity
                .ok()
                .lastModified(lastModified.getLastModified().getMillis())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(builder.build());
    }

    protected Set<String> processInclude(String include) {
        if (include == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(Sets.newHashSet(include.split(",")));
        }
    }
}
