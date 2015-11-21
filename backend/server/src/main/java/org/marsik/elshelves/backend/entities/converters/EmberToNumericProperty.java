package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
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
    public NumericProperty convert(String path, NumericPropertyApiModel object, NumericProperty model, Map<UUID, Object> cache, Set<String> include) {
        emberToNamedObject.convert(path, object, model, cache, include);

        model.setSymbol(object.getSymbol());
        model.setBase(object.getBase());
        model.setUnit(emberToUnit.convert(path, "unit", object.getUnit(), cache, include));

        return model;
    }
}
