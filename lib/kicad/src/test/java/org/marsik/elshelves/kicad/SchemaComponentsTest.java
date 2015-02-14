package org.marsik.elshelves.kicad;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class SchemaComponentsTest {

    @Test
    public void testFetchSingleComponent() throws Exception {
        SchemaComponents processor = new SchemaComponents();
        Collection<SchemaComponents.Component> components = processor.fetchComponents(getClass().getResourceAsStream("/ic.sch"));
        assertEquals(1, components.size());
    }

    @Test
    public void testFetchMultipleComponents() throws Exception {
        SchemaComponents processor = new SchemaComponents();
        Collection<SchemaComponents.Component> components = processor.fetchComponents(getClass().getResourceAsStream("/ic_two_units.sch"));
        assertEquals(2, components.size());
    }
}