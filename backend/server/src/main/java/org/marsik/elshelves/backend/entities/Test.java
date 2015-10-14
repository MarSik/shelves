package org.marsik.elshelves.backend.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Test {
    @GeneratedValue
    @Id
    Long id;
}
