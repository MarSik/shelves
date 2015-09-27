package org.marsik.elshelves.api.entities;

import lombok.Data;

import java.util.Set;

@Data
public class BackupApiModel {
    Set<FootprintApiModel> footprints;
    Set<SourceApiModel> sources;
    Set<PartTypeApiModel> types;
    Set<LotApiModel> lots;
    Set<ItemApiModel> items;
    Set<PartGroupApiModel> groups;
    Set<BoxApiModel> boxes;
    Set<RequirementApiModel> requirements;
    Set<TransactionApiModel> transactions;
    Set<PurchaseApiModel> purchases;
    Set<UnitApiModel> units;
    Set<NumericPropertyApiModel> properties;
    Set<DocumentApiModel> documents;
    UserApiModel user;
}
