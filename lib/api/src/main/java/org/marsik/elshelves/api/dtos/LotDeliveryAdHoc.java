package org.marsik.elshelves.api.dtos;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class LotDeliveryAdHoc {
    @NotNull
    UUID source;

    @NotNull
    UUID type;

    @NotNull
    UUID location;

    @NotNull
    Long count;
}
