package org.marsik.elshelves.backend.dtos;

import org.marsik.elshelves.backend.entities.Lot;

public class LotSplitResult {
	final Lot requested;
	final Lot remainder;

	public LotSplitResult(Lot requested, Lot remainder) {
		this.requested = requested;
		this.remainder = remainder;
	}

	public Lot getRequested() {
		return requested;
	}

	public Lot getRemainder() {
		return remainder;
	}
}
