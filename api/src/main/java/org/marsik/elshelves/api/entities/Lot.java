package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This entity represents one step in history of
 * a group of identical parts. It is read only!
 *
 * Any time a part is taken from the Lot, new Lot
 * objects need to be created to represent the
 * resulting two new Lots.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Lot extends AbstractEntity {
    UUID id;
    Date created;
    Long count;

    PartType type;
    Box location;

    Lot previous;
    List<Lot> next;

    User belongsTo;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("next", "next");
        return links;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Box getLocation() {
        return location;
    }

    @JsonIgnore
    public void setLocation(Box location) {
        this.location = location;
    }

    @JsonSetter
    public void setLocation(UUID location) {
        this.location = new Box();
        this.location.setId(location);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public PartType getType() {
        return type;
    }

    @JsonIgnore
    public void setType(PartType type) {
        this.type = type;
    }

    @JsonSetter
    public void setType(UUID type) {
        this.type = new PartType();
        this.type.setId(type);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Lot getPrevious() {
        return previous;
    }

    @JsonIgnore
    public void setPrevious(Lot previous) {
        this.previous = previous;
    }

    @JsonSetter
    public void setPrevious(UUID previous) {
        this.previous = new Lot();
        this.previous.setId(previous);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<Lot> getNext() {
        return next;
    }

    @JsonSetter
    public void setNext(List<Lot> next) {
        this.next = next;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public User getBelongsTo() {
        return belongsTo;
    }

    @JsonIgnore
    public void setBelongsTo(User belongsTo) {
        this.belongsTo = belongsTo;
    }

    @JsonSetter
    public void setBelongsTo(UUID belongsTo) {
        this.belongsTo = new User();
        this.belongsTo.setId(belongsTo);
    }
}
