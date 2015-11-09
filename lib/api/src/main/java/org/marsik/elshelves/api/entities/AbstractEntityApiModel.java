package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberEntity;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractEntityApiModel implements EmberEntity {
    public AbstractEntityApiModel(UUID uuid) {
        this.id = uuid;
    }

    @JsonProperty("id")
    UUID id;

    Long version;

    /**
     * This can be used as an explicit hint to the converter.
     * isStub() == true means this is an unresolved reference waiting
     * to be relinked.
     */
    @JsonIgnore
    boolean stub = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntityApiModel)) return false;

        AbstractEntityApiModel that = (AbstractEntityApiModel) o;

        if (id == null) {
            return this == o;
        } else if (that.getId() == null) {
            return this == o;
        } else {
            return this.id.equals(that.getId());
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
