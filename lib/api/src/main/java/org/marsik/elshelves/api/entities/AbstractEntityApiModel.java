package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberEntity;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.jackson.StubSupport;

import java.beans.Transient;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        defaultImpl = AbstractEntityApiModel.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(AbstractOwnedEntityApiModel.class),
        @JsonSubTypes.Type(AuthorizationApiModel.class),
        @JsonSubTypes.Type(CodeApiModel.class),
        @JsonSubTypes.Type(LotApiModel.class),
        @JsonSubTypes.Type(LotHistoryApiModel.class),
        @JsonSubTypes.Type(PurchaseApiModel.class),
        @JsonSubTypes.Type(RequirementApiModel.class),
        @JsonSubTypes.Type(UserApiModel.class)
})
@JsonTypeName("abstract")
public class AbstractEntityApiModel implements EmberEntity, StubSupport, Cloneable {
    public AbstractEntityApiModel(UUID uuid) {
        this.id = uuid;
    }

    public AbstractEntityApiModel(String uuid) {
        this(UUID.fromString(uuid));
        setStub(true);
    }

    @JsonProperty("id")
    UUID id;

    Long version;

    @JsonProperty("_type")
    String entityType;

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

    @JsonIgnore
    public String getEmberType() {
        String type = "unknown";

        JsonTypeName emberModelName = getClass().getAnnotation(JsonTypeName.class);
        if (emberModelName != null) {
            type = emberModelName.value();
        }

        return type;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @JsonIgnore
    public boolean isValid() {
        return id != null && version != null;
    }
}
