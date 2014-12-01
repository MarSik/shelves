package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.apache.commons.codec.binary.Hex;
import org.marsik.elshelves.backend.configuration.MailgunConfiguration;
import org.marsik.elshelves.backend.dtos.MailgunEmailReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

@Controller
@RequestMapping("/mail")
public class MailgunController {
    static final private Logger logger = LoggerFactory.getLogger(MailgunController.class);


    /**
     * Generic email receiver
     *
     * Checks and saves the received email. Sends a copy to all configured recipients
     * after the email is processed.
     *
     * If the email was sent by a member of the group, the Reply-To header in the copy for group members
     * will be set to the group's email. (discussion thread)
     *
     * If the email was sent by a non-member, the Reply-To header of the copy will be set to the global
     * response email address (and all emails to the global response address have to be processed by
     * the processResponseEmail method. (outside communication)
     *
     *
     * @param config
     * @param timestamp
     * @param token
     * @param signature
     * @return
     */
    @Transactional
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public ResponseEntity<String> processEmail(MailgunConfiguration config,
                                               @RequestParam("timestamp") Integer timestamp,
                                               @RequestParam("token") String token,
                                               @RequestParam("signature") String signature,
                                               @Valid MailgunEmailReceived emailReceived,
                                               HttpServletRequest request
                                               ) {
        if (!computeSignature(config.getKey(), timestamp, token).equals(signature)) {
            logger.warn("Failed Mailgun auth - key '{}', timestamp {}, token '{}', signature '{}'",
                    config.getKey(), timestamp, token, signature);
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }


        return new ResponseEntity<String>(HttpStatus.OK);
    }

    static public String computeSignature(String key, Integer timestamp, String token) {
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

    @RequestMapping(value = "/test")
    public ResponseEntity<String> logRequest(HttpServletRequest req) {
        StringBuilder result = new StringBuilder();

        result.append(req.getRequestURI()+"\n");
        result.append(req.getMethod()+"\n");

        try {
            String line;
            while ((line = req.getReader().readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {

        }

        logger.debug(result.toString());

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
