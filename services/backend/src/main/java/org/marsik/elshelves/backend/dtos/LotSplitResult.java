package org.marsik.elshelves.backend.dtos;

import org.marsik.elshelves.api.entities.LotApiModel;

public class LotSplitResult {
	final LotApiModel requested;
	final LotApiModel remainder;

	public LotSplitResult(LotApiModel requested, LotApiModel remainder) {
		this.requested = requested;
		this.remainder = remainder;
	}

	public LotApiModel getRequested() {
		return requested;
	}

	public LotApiModel getRemainder() {
		return remainder;
	}
}
