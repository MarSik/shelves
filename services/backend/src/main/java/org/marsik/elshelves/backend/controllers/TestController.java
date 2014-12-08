package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.Lot;
import org.marsik.elshelves.api.entities.PartGroup;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class TestController {
    @RequestMapping("/groups/{id}")
    public EmberModel getGroup(@PathVariable("id") Long id) {
        PartGroup g = new PartGroup(id, "TEST");
        return new EmberModel.Builder<PartGroup>(g).build();
    }

    @RequestMapping("/lots/{id}")
    public EmberModel getLot(@PathVariable("id") Long id) {
        Lot lot = new Lot();
        lot.setId(id);
        lot.setCreated(new Date());

        Lot l1 = new Lot();
        l1.setId(101l);

        Lot l2 = new Lot();
        l2.setId(102l);

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);

        return new EmberModel.Builder<Lot>(lot)
                .sideLoad(Lot.class, lots)
                .build();
    }

    @RequestMapping("/lots/{id}/next")
    public EmberModel getNextLots(@PathVariable("id") Long id) {
        Lot l1 = new Lot();
        l1.setId(101l);

        Lot l2 = new Lot();
        l2.setId(102l);

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);

        return new EmberModel.Builder<Lot>("next", lots).build();
    }
}
