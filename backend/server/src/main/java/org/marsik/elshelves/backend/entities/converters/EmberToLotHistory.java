package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToLotHistory extends AbstractEmberToEntity<LotHistoryApiModel, LotHistory> {
    public EmberToLotHistory() {
        super(LotHistory.class);
    }

    @Autowired
    EmberToBox emberToBox;

    @Autowired
    EmberToUser emberToUser;

    @Autowired
    EmberToRequirement emberToRequirement;

    @Override
    public LotHistory convert(String path, LotHistoryApiModel object, LotHistory model, Map<UUID, Object> cache, Set<String> include) {

        model.setValidSince(object.getValidSince());
        model.setId(object.getId());
        model.setPrevious(convert(path, "previous", object.getPrevious(), cache, include));
        model.setPerformedBy(emberToUser.convert(path, "performed-by", object.getPerformedBy(), cache, include));
        model.setAssignedTo(emberToRequirement.convert(path, "assigned-to", object.getAssignedTo(), cache, include));
        model.setLocation(emberToBox.convert(path, "location", object.getLocation(), cache, include));
        model.setAction(object.getAction());

        return model;
    }
}
