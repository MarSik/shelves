package org.marsik.elshelves.backend.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.marsik.elshelves.backend.configuration.MailgunConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MailgunService {
    @Autowired
    MailgunConfiguration configuration;

	@Autowired
	RestTemplate rest;

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

	private HttpHeaders prepareAuthHeaders() {
		String plainCreds = "api:" + configuration.getKey();
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		return headers;
	}

	static private class MailgunResponse {
		String message;
		String id;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

    public boolean sendVerificationCode(String email, String code) {

		String message = "Thanks for registering. Your verification code is: \n\n" + code + "\n\n"
				+ "You can confirm the account by clicking on the following link: \n\n"
				+ "https://www.shelves.cz/verification/" + code + "\n\n"
				+ "or by replying to this email.\n\n"
				+ "Regards\n\nShelves team\nadmin@shelves.cz";

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
		params.add("from", "Shelves.cz <admin@shelves.cz>");
		params.add("to", email);
		params.add("subject", "Shelves.cz account confirmation");
		params.add("h:Reply-To", "verify@shelves.cz");
		params.add("text", message);

		HttpHeaders headers = prepareAuthHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

		rest.postForObject("https://api.mailgun.net/v2/shelves.cz/messages",
				request, MailgunResponse.class);

        return true;
    }
}
