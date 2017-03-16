package org.marsik.elshelves.backend.app.prometheus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.prometheus.client.Collector;

import org.javamoney.moneta.Money;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.repositories.results.EntityMetric;
import org.marsik.elshelves.api.dtos.LotMetric;
import org.marsik.elshelves.backend.repositories.results.MoneySum;
import org.marsik.elshelves.backend.repositories.results.UserMetric;

public class ShelvesStatsCollector extends Collector {
    final LotRepository lotRepository;

    final UserRepository userRepository;

    final LoadingCache<Integer, List<MetricFamilySamples>> cache;

    public ShelvesStatsCollector(LotRepository lotRepository,
            UserRepository userRepository) {
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;

        cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, List<MetricFamilySamples>>() {
                    @Override
                    public List<MetricFamilySamples> load(Integer aVoid) throws Exception {
                        return compute();
                    }
                });
    }

    // !!! Return money and db stats only once per 10 minutes
    @Override
    public List<MetricFamilySamples> collect() {
        try {
            return cache.get(0);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<MetricFamilySamples> compute() {
        List<MetricFamilySamples> metrics = new ArrayList<>();

        LotMetric count = lotRepository.lotCount();

        List<MetricFamilySamples.Sample> sample = new ArrayList<>();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Collections.singletonList("type"),
                Collections.singletonList("total"),
                count.getTotal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Collections.singletonList("type"),
                Collections.singletonList("used"),
                count.getUsed()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Collections.singletonList("type"),
                Collections.singletonList("used_in_past"),
                count.getUsedInPast()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Collections.singletonList("type"),
                Collections.singletonList("assigned"),
                count.getAssigned()
        ));

        metrics.add(new MetricFamilySamples("shelves_part_count",
                Type.GAUGE, "Simple part count", sample));

        sample = new ArrayList<>();
        UserMetric userMetric = userRepository.userCount();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Collections.singletonList("type"),
                Collections.singletonList("total"),
                userMetric.getTotal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Collections.singletonList("type"),
                Collections.singletonList("local"),
                userMetric.getLocal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Collections.singletonList("type"),
                Collections.singletonList("federated"),
                userMetric.getFederated()
        ));

        metrics.add(new MetricFamilySamples("shelves_user_count",
                Type.GAUGE, "Simple user count", sample));


        EntityMetric entityMetric = lotRepository.totalCount();
        sample = new ArrayList<>();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_entity_count",
                Collections.singletonList("type"),
                Collections.singletonList("total"),
                entityMetric.getTotal()
        ));

        metrics.add(new MetricFamilySamples("shelves_entity_count",
                Type.GAUGE, "Database entity count", sample));

        final Stream<MoneySum> moneySumList = lotRepository.totalPricePaid();
        final ExchangeRateProvider
                provider = MonetaryConversions.getExchangeRateProvider("IDENT", "ECB-HIST", "ECB-HIST90", "ECB", "IMF");

        Money sum = moneySumList
                .map(i -> {
                    final Money m = Money.of(i.getAmount(), i.getCurrency());
                    final DateTime date = Optional.ofNullable(i.getDate()).orElse(i.getCreated());
                    final ConversionQuery query = ConversionQueryBuilder.of()
                            .setTermCurrency("CZK")
                            .set(LocalDate.class, date.toDate().toInstant().atZone(
                                    date.getZone().toTimeZone().toZoneId()).toLocalDate())
                            .build();

                    final CurrencyConversion conversion = provider.getCurrencyConversion(query);
                    return m.with(conversion);
                })
                .reduce(Money::add)
                .orElse(Money.of(0, "CZK"));

        sample = new ArrayList<>();
        sample.add(new MetricFamilySamples.Sample(
                "shelves_money_paid",
                Arrays.asList("type", "currency"),
                Arrays.asList("total", "CZK"),
                sum.getNumber().doubleValue()
        ));

        metrics.add(new MetricFamilySamples("shelves_money_paid",
                Type.GAUGE, "Total money paid", sample));

        return metrics;
    }
}
