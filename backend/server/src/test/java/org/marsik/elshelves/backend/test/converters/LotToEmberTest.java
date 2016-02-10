package org.marsik.elshelves.backend.test.converters;

import gnu.trove.map.hash.THashMap;
import org.junit.Ignore;
import org.junit.Test;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.*;

public class LotToEmberTest extends BaseConverterTest {
    @Autowired
    LotToEmber lotToEmber;

    @Test
    @Ignore
    public void baseTest() throws Exception {
        LotApiModel lot = lotToEmber.convert(new Lot(UUID.randomUUID()), new THashMap<>());
        assertNotNull(lot);
    }
}
