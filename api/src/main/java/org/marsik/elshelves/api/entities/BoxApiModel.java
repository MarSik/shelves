package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.deserializers.BoxIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.LotIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("box")
public class BoxApiModel extends AbstractEntityApiModel {
    @NotNull
    String name;
    CodeApiModel code;

    Set<LotApiModel> lots;

    UserApiModel belongsTo;
    BoxApiModel parent;
    Set<BoxApiModel> boxes;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("lots", "lots");
		links.put("boxes", "boxes");
        return links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<LotApiModel> getLots() {
        return lots;
    }

    @JsonSetter
	@JsonDeserialize(contentUsing = LotIdDeserializer.class)
    public void setLots(Set<LotApiModel> lots) {
        this.lots = lots;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getParent() {
        return parent;
    }

    @JsonSetter
	@JsonDeserialize(using = BoxIdDeserializer.class)
    public void setParent(BoxApiModel parent) {
        this.parent = parent;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Set<BoxApiModel> getBoxes() {
        return boxes;
    }

    @JsonSetter
	@JsonDeserialize(contentUsing = BoxIdDeserializer.class)
    public void setBoxes(Set<BoxApiModel> boxes) {
        this.boxes = boxes;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public CodeApiModel getCode() {
        return code;
    }

    @JsonIgnore
    public void setCode(CodeApiModel code) {
        this.code = code;
    }

    @JsonSetter
    public void setCode(UUID code) {
        this.code = new CodeApiModel();
        this.code.setId(code);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }

    @JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
    public void setBelongsTo(UserApiModel belongsTo) {
        this.belongsTo = belongsTo;
    }
}
