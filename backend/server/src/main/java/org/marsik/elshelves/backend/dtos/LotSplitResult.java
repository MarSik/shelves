package org.marsik.elshelves.backend.dtos;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.backend.entities.Lot;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LotSplitResult {
	final Lot requested;
	final Set<Lot> others;

	public LotSplitResult(Lot requested, Collection<Lot> others) {
		this.requested = requested;
		this.others = new THashSet<>();
		if (others != null) {
			this.others.addAll(others);
		}
		this.others.remove(requested);
	}

	public LotSplitResult(Lot requested, Map<Lot, Lot> others) {
		this.requested = requested;
		this.others = new THashSet<>();
		if (others != null) {
			this.others.addAll(others.keySet());
			this.others.addAll(others.values());
		}
		this.others.remove(requested);
	}

	public Lot getRequested() {
		return requested;
	}

	public Collection<Lot> getOthers() {
		return Collections.unmodifiableSet(others);
	}
}
