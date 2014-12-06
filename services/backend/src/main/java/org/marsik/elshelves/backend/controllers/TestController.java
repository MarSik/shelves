package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.PartGroup;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/groups/{id}")
    public PartGroup getGroup(@PathVariable("id") Integer id) {
        PartGroup g = new PartGroup(id, "TEST");
        return g;
    }
}
