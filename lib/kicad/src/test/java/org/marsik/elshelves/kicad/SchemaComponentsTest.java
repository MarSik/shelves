package org.marsik.elshelves.kicad;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SchemaComponentsTest {

    @Test
    public void testFetchSingleComponent() throws Exception {
        SchemaComponents processor = new SchemaComponents();
        Map<String, List<SchemaComponents.Component>> components = processor.fetchComponents(getClass().getResourceAsStream("/ic.sch"));
        assertEquals(1, components.size());
    }

    @Test
    public void testFetchMultipleComponents() throws Exception {
        SchemaComponents processor = new SchemaComponents();
        Map<String, List<SchemaComponents.Component>> components = processor.fetchComponents(getClass().getResourceAsStream("/ic_two_units.sch"));
        assertEquals(1, components.size());
        assertEquals(2, components.get("U?").size());
    }

    @Test
    public void testFetchPsuComponents() throws Exception {
        SchemaComponents processor = new SchemaComponents();
        Map<String, List<SchemaComponents.Component>> components = processor.fetchComponents(getClass().getResourceAsStream("/psu.sch"));
        assertEquals(68, components.size());
        assertEquals(2, components.get("U1").size());
        assertEquals(2, components.get("U2").size());
        assertEquals(1, components.get("U3").size());
    }
}