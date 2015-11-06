package org.marsik.elshelves.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.backend.interfaces.ContentEquals;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sku extends IdentifiedEntity implements ContentEquals<Sku> {
    @NotNull
    @NotEmpty
    String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    Type type;

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getSource(), this::setSource);
        relinkItem(relinker, getType(), this::setType);

        super.relink(relinker);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Transient
    public boolean sameContent(Sku sku) {
        EqualsBuilder builder = new EqualsBuilder();
        return builder
                .append(getSource(), sku.getSource())
                .append(getSku(), sku.getSku())
                .isEquals();
    }
}
