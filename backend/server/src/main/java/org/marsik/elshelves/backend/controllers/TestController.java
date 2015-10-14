package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.entities.Test;
import org.marsik.elshelves.backend.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    TestRepository testRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Transactional
    @RequestMapping("/transaction")
    @CircuitBreaker
    public Test generateTest() {
        Test t = new Test();
        testRepository.save(t);
        throw new IllegalArgumentException();
    }
}
