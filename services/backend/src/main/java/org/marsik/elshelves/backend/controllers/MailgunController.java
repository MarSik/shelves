package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.dtos.MailgunEmailReceived;
import org.marsik.elshelves.backend.services.MailgunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/mail")
public class MailgunController {
    static final private Logger logger = LoggerFactory.getLogger(MailgunController.class);

    @Autowired
    MailgunService mailgunService;

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
     * @param timestamp
     * @param token
     * @param signature
     * @return
     */
    @Transactional
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public ResponseEntity<String> processEmail(@RequestParam("timestamp") Integer timestamp,
                                               @RequestParam("token") String token,
                                               @RequestParam("signature") String signature,
                                               @Valid MailgunEmailReceived emailReceived,
                                               HttpServletRequest request
                                               ) {
        if (!mailgunService.computeSignature(timestamp, token).equals(signature)) {
            logger.warn("Failed Mailgun auth - key '{}', timestamp {}, token '{}', signature '{}'",
                    timestamp, token, signature);
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }


        return new ResponseEntity<String>(HttpStatus.OK);
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
