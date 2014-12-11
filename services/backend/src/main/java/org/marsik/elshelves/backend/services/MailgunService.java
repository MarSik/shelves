package org.marsik.elshelves.backend.services;

import org.apache.commons.codec.binary.Hex;
import org.marsik.elshelves.backend.configuration.MailgunConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MailgunService {
    @Autowired
    MailgunConfiguration configuration;

    public String computeSignature(Integer timestamp, String token) {
        String key = configuration.getKey();

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            String data = timestamp.toString()+token;
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (InvalidKeyException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public boolean sendVerificationCode(String email, String code) {
        return true;
    }
}
