package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
}
