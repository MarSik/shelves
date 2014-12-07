package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.PartGroup;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/groups/{id}")
    public EmberModel getGroup(@PathVariable("id") Long id) {
        PartGroup g = new PartGroup(id, "TEST");
        return new EmberModel.Builder<PartGroup>(g).build();
    }
}
