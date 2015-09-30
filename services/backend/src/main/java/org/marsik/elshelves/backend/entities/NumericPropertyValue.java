package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class NumericPropertyValue {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            optional = false)
    NamedEntity entity;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            optional = false)
    NumericProperty property;

    @NotNull
    Long value;
}
