package org.marsik.elshelves.api.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class BackupApiModel {
	Set<FootprintApiModel> footprints;
	Set<SourceApiModel> sources;
	Set<PartTypeApiModel> types;
	Set<LotApiModel> lots;
	Set<ProjectApiModel> projects;
	Set<PartGroupApiModel> groups;
	Set<BoxApiModel> boxes;
	Set<RequirementApiModel> requirements;
	Set<TransactionApiModel> transactions;
	Set<PurchaseApiModel> purchases;
    Set<UnitApiModel> units;
    Set<NumericPropertyApiModel> properties;
	UserApiModel user;
}
