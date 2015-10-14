package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/v1/footprinttypes")
public class FootprintTypeController {
    @RequestMapping
    @ResponseBody
    public EmberModel getAll(@RequestParam(value = "ids[]", required = false) String[] ids) throws EntityNotFound {
        Collection<FootprintType> allItems;

        if (ids == null) {
            allItems = Arrays.asList(FootprintType.values());
        } else {
            allItems = new THashSet<>();
            for (String id: ids) {
                allItems.add(FootprintType.valueOf(id));
            }
        }

        EmberModel.Builder<FootprintType> builder = new EmberModel.Builder<FootprintType>(FootprintType.class, allItems);
        return builder.build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    public EmberModel get(@PathVariable("id") FootprintType prefix) throws EntityNotFound {
        EmberModel.Builder<FootprintType> builder = new EmberModel.Builder<FootprintType>(prefix);
        return builder.build();
    }
}
