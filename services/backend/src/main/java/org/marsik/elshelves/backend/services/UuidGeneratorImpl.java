package org.marsik.elshelves.backend.services;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidGeneratorImpl implements UuidGenerator {
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
        return generator.generate();
    }
}
