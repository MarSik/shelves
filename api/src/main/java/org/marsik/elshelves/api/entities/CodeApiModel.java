package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;

import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("code")
public class CodeApiModel extends AbstractEntityApiModel {
    String code;

    BoxApiModel box;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getBox() {
        return box;
    }

    @JsonIgnore
    public void setBox(BoxApiModel box) {
        this.box = box;
    }

    @JsonSetter
    public void setBox(UUID box) {
        this.box = new BoxApiModel();
        this.box.setId(box);
    }
}
