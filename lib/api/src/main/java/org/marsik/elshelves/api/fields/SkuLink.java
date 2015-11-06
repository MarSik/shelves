package org.marsik.elshelves.api.fields;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SkuLink {
    UUID source;
    String sku;
}
