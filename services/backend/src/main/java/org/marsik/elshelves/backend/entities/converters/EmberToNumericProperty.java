package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToNumericProperty extends AbstractEmberToEntity<NumericPropertyApiModel, NumericProperty> {
    public EmberToNumericProperty() {
        super(NumericProperty.class);
    }

    @Autowired
    EmberToNamedObject emberToNamedObject;

    @Autowired
    EmberToUnit emberToUnit;

    @Override
    public NumericProperty convert(NumericPropertyApiModel object, NumericProperty model, int nested, Map<UUID, Object> cache) {
        emberToNamedObject.convert(object, model, nested, cache);

        if (nested == 0) {
            return model;
        }

        model.setBase(object.getBase());
        model.setUnit(emberToUnit.convert(object.getUnit(), nested - 1, cache));

        return model;
    }
}
