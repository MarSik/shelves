package org.marsik.elshelves.backend.dtos;

import org.marsik.elshelves.backend.entities.Lot;

public class LotSplitResult<T extends Lot> {
	final T requested;
	final T remainder;

	public LotSplitResult(T requested, T remainder) {
		this.requested = requested;
		this.remainder = remainder;
	}

	public T getRequested() {
		return requested;
	}

	public T getRemainder() {
		return remainder;
	}
}
