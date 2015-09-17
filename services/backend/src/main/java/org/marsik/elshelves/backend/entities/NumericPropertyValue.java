package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class NumericPropertyValue {
    @Id
    Long id;

    @ManyToOne
    NamedEntity entity;

    @ManyToOne
    NumericProperty property;

    @NotNull
    Long value;
}
