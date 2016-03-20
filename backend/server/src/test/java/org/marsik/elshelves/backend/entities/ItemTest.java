package org.marsik.elshelves.backend.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemTest {

    @org.junit.Test
    public void testUpdateFromFromLot() throws Exception {
        Item item = new Item();
        item.setUsed(false);
        item.setUsedInPast(false);
        item.setValid(true);

        Lot update = new Lot();
        Box location = new Box();
        update.setLocation(location);
        item.updateFrom(update);

        assertThat(item.getLocation())
                .isEqualTo(location);
    }

    @Test
    public void testIsRevisionNeededFromLot() throws Exception {
        Item item = new Item();
        item.setUsed(false);
        item.setUsedInPast(false);
        item.setValid(true);

        Lot update = new Lot();
        Box location = new Box();
        update.setLocation(location);
        boolean revisionNeeded = item.isRevisionNeeded(update);

        assertThat(revisionNeeded)
                .isTrue();
    }
}
