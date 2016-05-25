package org.marsik.elshelves.backend.repositories.results;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class MoneySum {
    private String currency;
    private BigDecimal amount;
    private DateTime date;
    private DateTime created;

    public MoneySum(String currency, BigDecimal amount, DateTime date, DateTime created) {
        this.currency = currency;
        this.amount = amount;
        this.date = date;
        this.created = created;
    }
}
