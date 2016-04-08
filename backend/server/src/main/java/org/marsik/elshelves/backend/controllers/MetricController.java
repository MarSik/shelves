package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.repositories.results.EntityMetric;
import org.marsik.elshelves.backend.repositories.results.LotMetric;
import org.marsik.elshelves.backend.repositories.results.UserMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/metrics")
public class MetricController {

    @Autowired
    LotRepository lotRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(produces = "text/plain")
    @ResponseBody
    public String generateMetric() {
        LotMetric count = lotRepository.lotCount();

        StringBuilder result = new StringBuilder();
        result.append("part_count{type=\"total\"} " + count.getTotal().toString() + '\n');
        if (count.getUsed() != null) result.append("part_count{type=\"used\"} " + count.getUsed().toString() + '\n');
        if (count.getUsedInPast() != null) result.append("part_count{type=\"used_in_past\"} " + count.getUsedInPast().toString() + '\n');

        UserMetric userMetric = userRepository.userCount();
        result.append("user_count{type=\"total\"} " + userMetric.getTotal().toString() + '\n');
        if (userMetric.getLocal() != null) result.append("user_count{type=\"local\"} " + userMetric.getLocal().toString() + '\n');
        if (userMetric.getFederated() != null) result.append("user_count{type=\"federated\"} " + userMetric.getFederated().toString() + '\n');

        EntityMetric entityMetric = lotRepository.totalCount();
        result.append("entity_count{type=\"total\"} " + entityMetric.getTotal().toString() + '\n');

        return result.toString();
    }
}
