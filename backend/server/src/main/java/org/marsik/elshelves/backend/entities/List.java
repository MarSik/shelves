package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.api.entities.ListApiModel;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DefaultEmberModel(ListApiModel.class)
public class List extends NamedEntity {
    @ManyToMany
    Set<IdentifiedEntity> items;

    public void addItem(IdentifiedEntity item) {
        items.add(item);
    }

    public void removeItem(IdentifiedEntity item) {
        items.remove(item);
    }

    @Override
    public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
        if (!(update0 instanceof List)) {
            throw new IllegalArgumentException();
        }

        List update = (List)update0;

        reconcileLists(update.getItems(), this::getItems, this::addItem, this::removeItem);

        super.updateFrom(update0);
    }

    @Override
    public void relink(Relinker relinker) {

        relinkList(relinker, this::getItems, this::addItem, this::removeItem);

        super.relink(relinker);
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
