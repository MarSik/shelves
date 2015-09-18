package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    NamedEntity entity;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    NumericProperty property;

    @NotNull
    Long value;
}
