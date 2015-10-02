package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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
    public NumericPropertyApiModel convert(NumericProperty object, NumericPropertyApiModel model, int nested, Map<UUID, Object> cache) {
        namedObjectToEmber.convert(object, model, nested, cache);

        if (nested == 0) {
            return model;
        }

        model.setSymbol(object.getSymbol());
        model.setBase(object.getBase());
        model.setUnit(unitToEmber.convert(object.getUnit(), nested - 1, cache));

        return model;
    }
}
