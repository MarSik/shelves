package org.marsik.elshelves.backend.dtos;

import java.util.Map;

import lombok.Value;
import org.marsik.elshelves.api.dtos.LotMetric;

@Value
public class UserInfo {
    Map<String, Double> moneyTracked;
    LotMetric partCount;
}
