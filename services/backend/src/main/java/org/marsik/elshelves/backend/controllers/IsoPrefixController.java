package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/isoprefixes")
public class IsoPrefixController {
    @RequestMapping
    @ResponseBody
    public EmberModel getAll(@RequestParam(value = "ids[]", required = false) String[] ids) throws EntityNotFound {
        Collection<IsoSizePrefix> allItems;

        if (ids == null) {
            allItems = Arrays.asList(IsoSizePrefix.values());
        } else {
            allItems = new THashSet<>();
            for (String id: ids) {
                allItems.add(IsoSizePrefix.valueOf(id));
            }
        }

        EmberModel.Builder<IsoSizePrefix> builder = new EmberModel.Builder<IsoSizePrefix>(IsoSizePrefix.class, allItems);
        return builder.build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    public EmberModel get(@PathVariable("id") IsoSizePrefix prefix) throws EntityNotFound {
        EmberModel.Builder<IsoSizePrefix> builder = new EmberModel.Builder<IsoSizePrefix>(prefix);
        return builder.build();
    }
}
