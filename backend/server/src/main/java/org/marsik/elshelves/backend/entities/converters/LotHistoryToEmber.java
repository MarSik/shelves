package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class LotHistoryToEmber extends AbstractEntityToEmber<LotHistory, LotHistoryApiModel> {
    public LotHistoryToEmber() {
        super(LotHistoryApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(LotHistory.class, this);

    }

    @Autowired
    UserToEmber userToEmber;

    @Autowired
    BoxToEmber boxToEmber;

    @Autowired
    RequirementToEmber requirementToEmber;

    @Autowired
    EntityToEmberConversionService conversionService;

    @Override
    public LotHistoryApiModel convert(String path, LotHistory object,
                                      LotHistoryApiModel model,
                                      Map<UUID, Object> cache, Set<String> include) {

        model.setId(object.getId());
        model.setAction(object.getAction());
        model.setValidSince(object.getValidSince());
        model.setPrevious(convert(path, "previous", object.getPrevious(), cache, include));
        model.setLocation(boxToEmber.convert(path, "location", object.getLocation(), cache, include));
        model.setPerformedBy(userToEmber.convert(path, "performed-by", object.getPerformedBy(), cache, include));
        model.setAssignedTo(requirementToEmber.convert(path, "assigned-to", object.getAssignedTo(), cache, include));

        return model;
    }
}
