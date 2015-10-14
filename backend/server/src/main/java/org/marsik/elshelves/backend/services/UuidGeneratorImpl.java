package org.marsik.elshelves.backend.services;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidGeneratorImpl implements UuidGenerator {
    private final static Logger log = LoggerFactory.getLogger(UuidGeneratorImpl.class);

    final NoArgGenerator generator;

    public UuidGeneratorImpl() {
        EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
        if (ethernetAddress == null) {
            ethernetAddress = EthernetAddress.constructMulticastAddress();
        }
        generator = Generators.timeBasedGenerator(ethernetAddress);
    }

    @Override
    public UUID generate() {
        final UUID uuid = generator.generate();
        log.debug("Generating UUID {}", uuid.toString());
        return uuid;
    }
}
