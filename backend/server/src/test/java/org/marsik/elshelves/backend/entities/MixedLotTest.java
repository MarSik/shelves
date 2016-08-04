package org.marsik.elshelves.backend.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.UUID;

import org.marsik.elshelves.backend.dtos.AncestryLevel;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;

public class MixedLotTest {
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    @org.junit.Test
    public void testComputeSimpleAncestry() throws Exception {
        Lot lot = new Lot();
        lot.setId(UUID.randomUUID());
        lot.setCount(5L);

        Collection<AncestryLevel> ancestry = lot.computeAncestry();
        assertThat(ancestry)
                .hasSize(1)
                .contains(AncestryLevel.builder()
                        .lot(lot.getId())
                        .count(1L)
                        .outOf(1L)
                        .build());
    }

    @org.junit.Test
    public void testComputeSimpleMixedAncestry() throws Exception {
        Lot lot1 = getNewLot(5L);
        Lot lot2 = getNewLot(3L);

        MixedLot mixed = MixedLot.from(uuidGenerator, lot1, lot2);

        Collection<AncestryLevel> ancestry = mixed.computeAncestry();
        assertThat(ancestry)
                .hasSize(3)
                .contains(AncestryLevel.builder()
                        .lot(mixed.getId())
                        .count(1L)
                        .outOf(1L)
                        .build())

                .contains(AncestryLevel.builder()
                        .lot(lot1.getId())
                        .count(lot1.getCount())
                        .outOf(lot1.getCount() + lot2.getCount())
                        .build())
                .contains(AncestryLevel.builder()
                        .lot(lot2.getId())
                        .count(lot2.getCount())
                        .outOf(lot1.getCount() + lot2.getCount())
                        .build());
    }

    @org.junit.Test
    public void testComputeNestedMixedAncestry() throws Exception {
        Lot lot1 = getNewLot(1L);
        Lot lot2 = getNewLot(2L);
        MixedLot mixed1 = MixedLot.from(uuidGenerator, lot1, lot2);

        Lot lot3 = getNewLot(4L);
        Lot lot4 = getNewLot(8L);
        MixedLot mixed2 = MixedLot.from(uuidGenerator, lot3, lot4);

        MixedLot mixed = MixedLot.from(uuidGenerator, mixed1, mixed2);

        Collection<AncestryLevel> ancestry = mixed.computeAncestry();
        assertThat(ancestry)
                .hasSize(7)
                .contains(AncestryLevel.builder()
                        .lot(mixed.getId())
                        .count(1L)
                        .outOf(1L)
                        .build())

                // Mixed parents
                .contains(AncestryLevel.builder()
                        .lot(mixed1.getId())
                        .count(mixed1.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize())
                .contains(AncestryLevel.builder()
                        .lot(mixed2.getId())
                        .count(mixed2.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize())

                // Leaf lots
                .contains(AncestryLevel.builder()
                        .lot(lot1.getId())
                        .count(lot1.getCount())
                        .outOf(mixed.getCount())
                        .build())
                .contains(AncestryLevel.builder()
                        .lot(lot2.getId())
                        .count(lot2.getCount())
                        .outOf(mixed.getCount())
                        .build())
                .contains(AncestryLevel.builder()
                        .lot(lot3.getId())
                        .count(lot3.getCount())
                        .outOf(mixed.getCount())
                        .build())
                .contains(AncestryLevel.builder()
                        .lot(lot4.getId())
                        .count(lot4.getCount())
                        .outOf(mixed.getCount())
                        .build());
    }

    @org.junit.Test
    public void testComputeCommonParentMixedAncestry() throws Exception {
        Lot lot1 = getNewLot(4L);
        Lot lot2 = getNewLot(8L);
        MixedLot mixed1 = MixedLot.from(uuidGenerator, lot1, lot2);
        MixedLot mixed2 = (MixedLot)mixed1.shallowClone();
        mixed2.setId(uuidGenerator.generate());

        mixed1.setCount(3L);
        mixed2.setCount(9L);

        MixedLot mixed = MixedLot.from(uuidGenerator, mixed1, mixed2);

        Collection<AncestryLevel> ancestry = mixed.computeAncestry();
        assertThat(ancestry)
                .hasSize(5)
                .contains(AncestryLevel.builder()
                        .lot(mixed.getId())
                        .count(1L)
                        .outOf(1L)
                        .build())

                // Mixed parents
                .contains(AncestryLevel.builder()
                        .lot(mixed1.getId())
                        .count(mixed1.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize())
                .contains(AncestryLevel.builder()
                        .lot(mixed2.getId())
                        .count(mixed2.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize())

                // Leaf lots
                .contains(AncestryLevel.builder()
                        .lot(lot1.getId())
                        .count(lot1.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize())
                .contains(AncestryLevel.builder()
                        .lot(lot2.getId())
                        .count(lot2.getCount())
                        .outOf(mixed.getCount())
                        .build().normalize());
    }

    private Lot getNewLot(Long count) {
        Lot lot = new Lot();
        lot.setUsed(false);
        lot.setUsedInPast(false);
        lot.setCount(count);
        lot.setValid(true);
        lot.setId(uuidGenerator.generate());
        return lot;
    }
}
