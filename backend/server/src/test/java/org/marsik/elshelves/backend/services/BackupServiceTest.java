package org.marsik.elshelves.backend.services;

import lombok.Data;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
            //m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return m;
        }
    }

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    ModelMapper modelMapper;

    @Ignore
    @Test
    public void testHistoryToPrevious() {
        LotHistoryApiModel history = new LotHistoryApiModel();
        history.setId(uuidGenerator.generate());
        history.setPreviousId(uuidGenerator.generate());
        history.setAction(LotAction.DESTROYED);
        history.setCreated(new DateTime());
        history.setPerformedById(uuidGenerator.generate());
        history.setLocationId(uuidGenerator.generate());

        LotHistory h = modelMapper.map(history, LotHistory.class);

        assertThat(h.getPerformedBy())
                .isInstanceOf(Person.class);
        assertThat(h.getPerformedBy().getId())
                .isEqualTo(history.getPerformedById());

        assertThat(h.getLocation())
                .isInstanceOf(Box.class);
        assertThat(h.getLocation().getId())
                .isEqualTo(history.getLocationId());

        assertThat(h.getPrevious())
                .isInstanceOf(LotHistory.class);
        assertThat(h.getPrevious().getId())
                .isEqualTo(history.getPreviousId());
    }

    @Data
    private static class Action {
        UUID id;
        Action previous;
        Person user;
    }

    @Data
    private static class Person {
        UUID id;
    }

    @Data
    private static class ActionDTO {
        UUID id;
        UUID previousId;
        UUID userId;
    }

    // ModelMapper does not support recursive references
    // see https://github.com/jhalterman/modelmapper/issues/164

    @Ignore
    @Test
    public void testPreviousLink() {
        ActionDTO dto = new ActionDTO();
        dto.setPreviousId(UUID.randomUUID());
        dto.setId(UUID.randomUUID());

        Action model = modelMapper.map(dto, Action.class);

        assertThat(model)
                .isInstanceOf(Action.class);

        assertThat(model.getId())
                .isEqualTo(dto.getId());

        assertThat(model.getPrevious())
                .isNotNull()
                .isInstanceOf(Action.class);

        assertThat(model.getPrevious().getId())
                .isEqualTo(dto.getPreviousId());
    }

    @Ignore
    @Test
    public void testPreviousLinkToEmber() {
        Action dto = new Action();
        dto.setPrevious(new Action());
        dto.setId(UUID.randomUUID());
        dto.getPrevious().setId(UUID.randomUUID());
        dto.setUser(new Person());
        dto.getUser().setId(UUID.randomUUID());

        ActionDTO model = modelMapper.map(dto, ActionDTO.class);

        assertThat(model)
                .isInstanceOf(ActionDTO.class);

        assertThat(model.getId())
                .isEqualTo(dto.getId());

        assertThat(model.getUserId())
                .isNotNull()
                .isInstanceOf(UUID.class)
                .isEqualTo(dto.getUser().getId());

        assertThat(model.getPreviousId())
                .isNotNull()
                .isInstanceOf(UUID.class)
                .isEqualTo(dto.getPrevious().getId());
    }
}
