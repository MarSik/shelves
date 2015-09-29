package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToLotHistory extends AbstractEmberToEntity<LotHistoryApiModel, LotHistory> {
    public EmberToLotHistory() {
        super(LotHistory.class);
    }

    @Override
    public LotHistory convert(LotHistoryApiModel object, LotHistory model, int nested, Map<UUID, Object> cache) {
        model.setLocation(new Box());
        model.getLocation().setId(object.getLocationId());

        model.setCreated(object.getCreated());
        model.setId(object.getId());

        model.setPrevious(new LotHistory());
        model.getPrevious().setId(object.getPreviousId());

        model.setPerformedBy(new User());
        model.getPerformedBy().setId(object.getPerformedById());

        model.setAction(object.getAction());

        return model;
    }
}
