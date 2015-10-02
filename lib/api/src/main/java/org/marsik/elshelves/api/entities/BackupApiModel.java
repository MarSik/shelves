package org.marsik.elshelves.api.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;

import java.util.Set;

@Data
public class BackupApiModel {
    Set<FootprintApiModel> footprints = new THashSet<>();
    Set<SourceApiModel> sources = new THashSet<>();
    Set<PartTypeApiModel> types = new THashSet<>();
    Set<LotApiModel> lots = new THashSet<>();
    Set<LotHistoryApiModel> history = new THashSet<>();
    Set<ItemApiModel> items = new THashSet<>();
    Set<PartGroupApiModel> groups = new THashSet<>();
    Set<BoxApiModel> boxes = new THashSet<>();
    Set<RequirementApiModel> requirements = new THashSet<>();
    Set<TransactionApiModel> transactions = new THashSet<>();
    Set<PurchaseApiModel> purchases = new THashSet<>();
    Set<UnitApiModel> units = new THashSet<>();
    Set<NumericPropertyApiModel> properties = new THashSet<>();
    Set<DocumentApiModel> documents = new THashSet<>();
    UserApiModel user;
}
