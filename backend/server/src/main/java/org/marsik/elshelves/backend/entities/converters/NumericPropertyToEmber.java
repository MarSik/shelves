package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class NumericPropertyToEmber extends AbstractEntityToEmber<NumericProperty, NumericPropertyApiModel> {
    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    UnitToEmber unitToEmber;

    public NumericPropertyToEmber() {
        super(NumericPropertyApiModel.class);
    }

    @Override
    public NumericPropertyApiModel convert(String path, NumericProperty object, NumericPropertyApiModel model, Map<UUID, Object> cache, Set<String> include) {
        namedObjectToEmber.convert(path, object, model, cache, include);


        model.setSymbol(object.getSymbol());
        model.setBase(object.getBase());
        model.setUnit(unitToEmber.convert(path, "unit", object.getUnit(), cache, include));

        return model;
    }
}
