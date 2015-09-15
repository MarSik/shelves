package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class NumericPropertyValue {
    @NotNull
    NamedEntity entity;

    @NotNull
    NumericProperty property;

    @NotNull
    Long value;
}
