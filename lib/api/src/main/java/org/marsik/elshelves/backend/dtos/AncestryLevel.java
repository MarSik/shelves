package org.marsik.elshelves.backend.dtos;

import java.math.BigInteger;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class AncestryLevel {
    UUID lot;
    Long count;
    Long outOf;

    public AncestryLevel parent(AncestryLevel level) {

		final long newCount = getCount() * level.getCount();
		final long newOutOf = getOutOf() * level.getOutOf();

		return AncestryLevel.builder()
                .lot(level.getLot())
                .count(newCount)
                .outOf(newOutOf)
                .build();
    }

    public AncestryLevel add(AncestryLevel other) {
        if (!getLot().equals(other.getLot())) {
            throw new IllegalArgumentException();
        }

		AncestryLevel one = this.sameBase(other);
		AncestryLevel two = other.sameBase(this);

		return AncestryLevel.builder()
                .lot(getLot())
				.count(one.getCount() + two.getCount())
				.outOf(one.getOutOf())
                .build();
    }

	public AncestryLevel normalize() {
		long gcd = BigInteger.valueOf(getCount()).gcd(BigInteger.valueOf(getOutOf())).longValue();

		return AncestryLevel.builder()
				.lot(getLot())
				.count(getCount() / gcd)
				.outOf(getOutOf() / gcd)
				.build();
	}

	public Long commonBase(AncestryLevel other) {
		long gcd = BigInteger.valueOf(getOutOf()).gcd(BigInteger.valueOf(other.getOutOf())).longValue();
		return gcd * (getOutOf() / gcd) * (other.getOutOf() / gcd);
	}

	public AncestryLevel sameBase(AncestryLevel other) {
		long gcd = BigInteger.valueOf(getOutOf()).gcd(BigInteger.valueOf(other.getOutOf())).longValue();
		return AncestryLevel.builder()
				.lot(getLot())
				.count(getCount() * (other.getOutOf() / gcd))
				.outOf(getOutOf() * (other.getOutOf() / gcd))
				.build();
	}
}
