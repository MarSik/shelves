package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Requirement;
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
        if (object.getLocationId() != null) {
            model.setLocation(new Box());
            model.getLocation().setId(object.getLocationId());
        }

        model.setValidSince(object.getValidSince());
        model.setId(object.getId());

        if (object.getPreviousId() != null) {
            model.setPrevious(new LotHistory());
            model.getPrevious().setId(object.getPreviousId());
        }

        if (object.getPerformedById() != null) {
            model.setPerformedBy(new User());
            model.getPerformedBy().setId(object.getPerformedById());
        }

        if (object.getAssignedToId() != null) {
            model.setAssignedTo(new Requirement());
            model.getAssignedTo().setId(object.getAssignedToId());
        }

        model.setAction(object.getAction());

        return model;
    }
}
