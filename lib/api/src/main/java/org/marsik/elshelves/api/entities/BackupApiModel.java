package org.marsik.elshelves.api.entities;

import java.util.Set;

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
	Set<DocumentApiModel> documents;
	UserApiModel user;

	public Set<FootprintApiModel> getFootprints() {
		return footprints;
	}

	public void setFootprints(Set<FootprintApiModel> footprints) {
		this.footprints = footprints;
	}

	public Set<SourceApiModel> getSources() {
		return sources;
	}

	public void setSources(Set<SourceApiModel> sources) {
		this.sources = sources;
	}

	public Set<PartTypeApiModel> getTypes() {
		return types;
	}

	public void setTypes(Set<PartTypeApiModel> types) {
		this.types = types;
	}

	public Set<LotApiModel> getLots() {
		return lots;
	}

	public void setLots(Set<LotApiModel> lots) {
		this.lots = lots;
	}

	public Set<ProjectApiModel> getProjects() {
		return projects;
	}

	public void setProjects(Set<ProjectApiModel> projects) {
		this.projects = projects;
	}

	public Set<PartGroupApiModel> getGroups() {
		return groups;
	}

	public void setGroups(Set<PartGroupApiModel> groups) {
		this.groups = groups;
	}

	public Set<BoxApiModel> getBoxes() {
		return boxes;
	}

	public void setBoxes(Set<BoxApiModel> boxes) {
		this.boxes = boxes;
	}

	public Set<RequirementApiModel> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<RequirementApiModel> requirements) {
		this.requirements = requirements;
	}

	public Set<TransactionApiModel> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<TransactionApiModel> transactions) {
		this.transactions = transactions;
	}

	public Set<PurchaseApiModel> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<PurchaseApiModel> purchases) {
		this.purchases = purchases;
	}

    public Set<UnitApiModel> getUnits() {
        return units;
    }

    public void setUnits(Set<UnitApiModel> units) {
        this.units = units;
    }

    public Set<NumericPropertyApiModel> getProperties() {
        return properties;
    }

    public void setProperties(Set<NumericPropertyApiModel> properties) {
        this.properties = properties;
    }

    public UserApiModel getUser() {
		return user;
	}

	public void setUser(UserApiModel user) {
		this.user = user;
	}

	public Set<DocumentApiModel> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<DocumentApiModel> documents) {
		this.documents = documents;
	}
}
