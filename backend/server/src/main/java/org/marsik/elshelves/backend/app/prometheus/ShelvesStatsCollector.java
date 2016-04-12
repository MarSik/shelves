package org.marsik.elshelves.backend.app.prometheus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.prometheus.client.Collector;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.repositories.results.EntityMetric;
import org.marsik.elshelves.backend.repositories.results.LotMetric;
import org.marsik.elshelves.backend.repositories.results.UserMetric;

public class ShelvesStatsCollector extends Collector {
    LotRepository lotRepository;

    UserRepository userRepository;

    public ShelvesStatsCollector(LotRepository lotRepository,
            UserRepository userRepository) {
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> metrics = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        LotMetric count = lotRepository.lotCount();

        List<MetricFamilySamples.Sample> sample = new ArrayList<>();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Arrays.asList("type"),
                Arrays.asList("total"),
                count.getTotal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Arrays.asList("type"),
                Arrays.asList("used"),
                count.getUsed()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_part_count",
                Arrays.asList("type"),
                Arrays.asList("used_in_past"),
                count.getUsedInPast()
        ));

        metrics.add(new MetricFamilySamples("shelves_part_count",
                Type.GAUGE, "Simple part count", sample));

        sample = new ArrayList<>();
        UserMetric userMetric = userRepository.userCount();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Arrays.asList("type"),
                Arrays.asList("total"),
                userMetric.getTotal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Arrays.asList("type"),
                Arrays.asList("local"),
                userMetric.getLocal()
        ));

        sample.add(new MetricFamilySamples.Sample(
                "shelves_user_count",
                Arrays.asList("type"),
                Arrays.asList("federated"),
                userMetric.getFederated()
        ));

        metrics.add(new MetricFamilySamples("shelves_user_count",
                Type.GAUGE, "Simple user count", sample));


        EntityMetric entityMetric = lotRepository.totalCount();
        sample = new ArrayList<>();

        sample.add(new MetricFamilySamples.Sample(
                "shelves_entity_count",
                Arrays.asList("type"),
                Arrays.asList("total"),
                entityMetric.getTotal()
        ));

        metrics.add(new MetricFamilySamples("shelves_entity_count",
                Type.GAUGE, "Database entity count", sample));

        return metrics;
    }
}
