package org.marsik.elshelves.api.entities;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BackupApiModel {
    Set<FootprintApiModel> footprints = new HashSet<>();
    Set<SourceApiModel> sources = new HashSet<>();
    Set<PartTypeApiModel> types = new HashSet<>();
    Set<LotApiModel> lots = new HashSet<>();
    Set<LotHistoryApiModel> history = new HashSet<>();
    Set<ItemApiModel> items = new HashSet<>();
    Set<PartGroupApiModel> groups = new HashSet<>();
    Set<BoxApiModel> boxes = new HashSet<>();
    Set<RequirementApiModel> requirements = new HashSet<>();
    Set<TransactionApiModel> transactions = new HashSet<>();
    Set<PurchaseApiModel> purchases = new HashSet<>();
    Set<UnitApiModel> units = new HashSet<>();
    Set<NumericPropertyApiModel> properties = new HashSet<>();
    Set<DocumentApiModel> documents = new HashSet<>();
    UserApiModel user;
}
