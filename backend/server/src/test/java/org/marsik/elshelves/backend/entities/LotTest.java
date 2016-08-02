package org.marsik.elshelves.backend.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LotTest {
    private Box location;
    private Requirement requirement;
    private Lot lot;

    @Test
    public void testAssignment() throws Exception {
        Lot update = new Lot();
        update.setUsedBy(requirement);

        lot.updateFrom(update);

        assertThat(lot.getUsed())
                .isFalse();
        assertThat(lot.getUsedBy())
                .isEqualTo(requirement);
        assertThat(lot.getLocation())
                .isEqualTo(location);
    }

    @Test
    public void testAssignmentWithUsedFlag() throws Exception {
        Lot update = new Lot();
        update.setUsedBy(requirement);
        update.setUsed(true);

        lot.updateFrom(update);

        assertThat(lot.getUsed())
                .isTrue();
        assertThat(lot.getUsedBy())
                .isEqualTo(requirement);
        assertThat(lot.getLocation())
                .isNull();
    }

    @Before
    public void setUp() {
        location = new Box();
        Item item = new Item();
        item.setLocation(location);
        requirement = new Requirement();
        requirement.setItem(item);

        lot = new Lot();
        lot.setValid(true);
        lot.setUsed(false);
        lot.setUsedInPast(false);
        lot.setLocation(location);
    }

    @Test
    public void testUnAssignmentByRequirement() throws Exception {
        lot.setUsedBy(requirement);
        lot.setUsed(false);
        lot.setUsedInPast(false);

        Lot update = new Lot();
        update.setUsedBy(null);

        lot.updateFrom(update);

        assertThat(lot.getUsed())
                .isFalse();
        assertThat(lot.getUsedBy())
                .isNull();
        assertThat(lot.getUsedInPast())
                .isFalse();
        assertThat(lot.getLocation())
                .isEqualTo(location);
    }

    @Test
    public void testUnsoldering() throws Exception {
        lot.setUsedBy(requirement);
        lot.setUsed(true);
        lot.setUsedInPast(true);
        lot.setLocation(null);

        Lot update = new Lot();
        update.setUsed(false);

        lot.updateFrom(update);

        assertThat(lot.getUsed())
                .isFalse();
        assertThat(lot.getUsedBy())
                .isNull();
        assertThat(lot.getUsedInPast())
                .isTrue();
        assertThat(lot.getLocation())
                .isEqualTo(location);
    }

    @Test
    public void testSoldering() throws Exception {
        lot.setUsedBy(requirement);
        lot.setUsed(false);

        Lot update = new Lot();
        update.setUsed(true);
        update.setUsedBy(requirement);

        lot.updateFrom(update);

        assertThat(lot.getUsed())
                .isTrue();
        assertThat(lot.getUsedBy())
                .isEqualTo(requirement);
        assertThat(lot.getUsedInPast())
                .isTrue();
        assertThat(lot.getLocation())
                .isNull();
    }
}
