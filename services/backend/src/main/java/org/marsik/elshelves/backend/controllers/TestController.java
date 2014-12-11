package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.Lot;
import org.marsik.elshelves.api.entities.PartGroup;
import org.marsik.elshelves.api.entities.User;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.MailgunService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class TestController {
    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    ElshelvesUserDetailsService userDetailsService;

    @Autowired
    MailgunService mailgunService;

    @RequestMapping("/groups/{id}")
    public EmberModel getGroup(@PathVariable("id") UUID id) {
        PartGroup g = new PartGroup(id, "TEST");
        return new EmberModel.Builder<PartGroup>(g).build();
    }

    @RequestMapping("/lots/{id}")
    public EmberModel getLot(@PathVariable("id") UUID id) {
        Lot lot = new Lot();
        lot.setId(id);
        lot.setCreated(new Date());

        Lot l1 = new Lot();
        l1.setId(uuidGenerator.generate());

        Lot l2 = new Lot();
        l2.setId(uuidGenerator.generate());

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);
        lot.setNext(lots);

        return new EmberModel.Builder<Lot>(lot)
                .sideLoad(Lot.class, lots)
                .build();
    }

    @RequestMapping("/lots/{id}/next")
    public EmberModel getNextLots(@PathVariable("id") UUID id) {
        Lot l1 = new Lot();
        l1.setId(uuidGenerator.generate());

        Lot l2 = new Lot();
        l2.setId(uuidGenerator.generate());

        List<Lot> lots = new ArrayList<>();
        lots.add(l1);
        lots.add(l2);

        return new EmberModel.Builder<Lot>(Lot.class, lots).build();
    }

    @Transactional
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<Void> registerUser(@RequestBody @Valid User user) {
        if (userDetailsService.loadUserByUsername(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String verificationCode = userDetailsService.createUser(user);
        mailgunService.sendVerificationCode(user.getEmail(), verificationCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @RequestMapping(value = "/users/verify/{code}", method = RequestMethod.POST)
    public EmberModel verifyUser(@PathVariable("code") String code) {
        String password = userDetailsService.verifyUser(code);
        return new EmberModel.Builder<String>(password).build();
    }
}
