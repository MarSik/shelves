package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/siprefixes")
public class SiPrefixController {
    @RequestMapping
    @ResponseBody
    public EmberModel getAll(@RequestParam(value = "ids[]", required = false) String[] ids) throws EntityNotFound {
        Collection<SiPrefix> allItems;

        if (ids == null) {
            allItems = Arrays.asList(SiPrefix.values());
        } else {
            allItems = new THashSet<>();
            for (String id: ids) {
                allItems.add(SiPrefix.valueOf(id));
            }
        }

        EmberModel.Builder<SiPrefix> builder = new EmberModel.Builder<SiPrefix>(SiPrefix.class, allItems);
        return builder.build();
    }

    @RequestMapping("/{id}")
    @ResponseBody
    public EmberModel get(@PathVariable("id") SiPrefix prefix) throws EntityNotFound {
        EmberModel.Builder<SiPrefix> builder = new EmberModel.Builder<SiPrefix>(prefix);
        return builder.build();
    }
}
