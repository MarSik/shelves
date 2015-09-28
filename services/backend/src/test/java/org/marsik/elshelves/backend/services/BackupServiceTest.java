package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BackupServiceTest {

    @Configuration
    static class TestConfig {
        @Bean
        public UuidGenerator uuidGenerator() {
            return new UuidGeneratorImpl();
        }

        @Bean
        public ModelMapper modelMapper() {
            ModelMapper m = new ModelMapper();
            return m;
        }
    }

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void testHistoryToPrevious() {
        LotHistoryApiModel history = new LotHistoryApiModel();
        history.setId(uuidGenerator.generate());
        history.setPreviousId(uuidGenerator.generate());
        history.setAction(LotAction.DESTROYED);
        history.setCreated(new DateTime());
        history.setPerformedById(uuidGenerator.generate());

        LotHistory h = modelMapper.map(history, LotHistory.class);

        assertThat(h.getPrevious())
                .isInstanceOf(LotHistory.class);
        assertThat(h.getPrevious().getId())
                .isEqualTo(history.getPreviousId());

        assertThat(h.getPerformedBy())
                .isInstanceOf(User.class);
        assertThat(h.getPerformedBy().getId())
                .isEqualTo(history.getPerformedById());
    }
}
