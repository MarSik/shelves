package org.marsik.elshelves.api.entities;

import org.marsik.elshelves.api.ember.EmberModelName;

@EmberModelName("group")
public class PartGroup extends AbstractEntity {
    Long id;
    String name;

    public PartGroup(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
