package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Code extends AbstractEntity {
    Long id;
    String code;

    Box box;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Box getBox() {
        return box;
    }

    @JsonIgnore
    public void setBox(Box box) {
        this.box = box;
    }

    @JsonSetter
    public void setBox(Long box) {
        this.box = new Box();
        this.box.setId(box);
    }
}
