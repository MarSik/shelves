package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.idresolvers.IsoSizePrefixDeserializer;

public class NumericPropertyValueApiModel {
    NumericPropertyApiModel property;
    Long value;
    Long fraction;
    Long fractionDivider;
    IsoSizePrefix isoPrefix;

    @JsonIdentityReference(alwaysAsId = true)
    public NumericPropertyApiModel getProperty() {
        return property;
    }

    public void setProperty(NumericPropertyApiModel property) {
        this.property = property;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getFraction() {
        return fraction;
    }

    public void setFraction(Long fraction) {
        this.fraction = fraction;
    }

    public Long getFractionDivider() {
        return fractionDivider;
    }

    public void setFractionDivider(Long fractionDivider) {
        this.fractionDivider = fractionDivider;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public IsoSizePrefix getIsoPrefix() {
        return isoPrefix;
    }

    @JsonDeserialize(using = IsoSizePrefixDeserializer.class)
    public void setIsoPrefix(IsoSizePrefix isoPrefix) {
        this.isoPrefix = isoPrefix;
    }
}
