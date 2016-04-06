package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.results.LotCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/metric")
public class MetricController {

    @Autowired
    LotRepository lotRepository;

    @RequestMapping(produces = "text/plain")
    @ResponseBody
    public String generateMetric() {
        LotCount count = lotRepository.lotCount();

        StringBuilder result = new StringBuilder();
        result.append("part_count{type=\"total\"} " + count.getTotal().toString() + '\n');
        if (count.getUsed() != null) result.append("part_count{type=\"used\"} " + count.getUsed().toString() + '\n');
        if (count.getUsedInPast() != null) result.append("part_count{type=\"used_in_past\"} " + count.getUsedInPast().toString() + '\n');

        return result.toString();
    }
}
