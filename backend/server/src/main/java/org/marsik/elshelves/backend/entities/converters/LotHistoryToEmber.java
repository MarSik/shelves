package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class LotHistoryToEmber extends AbstractEntityToEmber<LotHistory, LotHistoryApiModel> {
    public LotHistoryToEmber() {
        super(LotHistoryApiModel.class);
    }

    @Override
    public LotHistoryApiModel convert(LotHistory object,
            LotHistoryApiModel model,
            int nested,
            Map<UUID, Object> cache) {

        model.setId(object.getId());
        model.setAction(object.getAction());
        model.setCreated(object.getCreated());
        model.setPreviousId(object.getPrevious() != null ? object.getPrevious().getId() : null);

        model.setLocationId(object.getLocation() != null ? object.getLocation().getId() : null);
        model.setPerformedById(object.getPerformedBy() != null ? object.getPerformedBy().getId() : null);
        model.setAssignedToId(object.getAssignedTo() != null ? object.getAssignedTo().getId() : null);

        return model;
    }
}
