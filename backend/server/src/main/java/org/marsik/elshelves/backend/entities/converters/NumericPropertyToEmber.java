package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class NumericPropertyToEmber extends AbstractEntityToEmber<NumericProperty, NumericPropertyApiModel> {
    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    UnitToEmber unitToEmber;

    @Autowired
    EntityToEmberConversionService conversionService;

    public NumericPropertyToEmber() {
        super(NumericPropertyApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(NumericProperty.class, getTarget(), this);

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
