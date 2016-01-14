package org.marsik.elshelves.backend.services;

public interface MailService {
    boolean sendVerificationCode(String email, String code);
    String computeSignature(Integer timestamp, String token);
}
