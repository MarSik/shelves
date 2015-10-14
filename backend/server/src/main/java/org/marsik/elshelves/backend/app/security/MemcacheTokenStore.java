package org.marsik.elshelves.backend.app.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.spy.memcached.MemcachedClient;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class MemcacheTokenStore implements TokenStore {
    private static final Logger log = LoggerFactory.getLogger(MemcacheTokenStore.class);

    @Autowired
    MemcachedClient memcachedClient;

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    @Autowired
    ElshelvesUserDetailsService userService;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private static final String TOKEN_BY_AUTH = "TA+";
    private static final String ACCESS_TOKEN = "AC+";
    private static final String AUTH_TOKEN = "AU+";
    private static final String REFRESH_TOKEN = "RF+";
    private static final String REFRESH_AUTH_TOKEN = "RA+";

    private static final String TOKENS_BY_ID = "TI+";
    private static final String TOKENS_BY_LOGIN = "TL+";

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return getAuth(memcachedClient.get(AUTH_TOKEN + token));
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String serializedToken = serialize(token);

        memcachedClient.add(ACCESS_TOKEN+token.getValue(), token.getExpiresIn(), serializedToken);
        memcachedClient.add(AUTH_TOKEN+token.getValue(), token.getExpiresIn(), serialize(authentication));
        memcachedClient.add(TOKEN_BY_AUTH+authenticationKeyGenerator.extractKey(authentication),
                token.getExpiresIn(), serializedToken);

        memcachedClient.set(TOKENS_BY_ID + authentication.getOAuth2Request().getClientId(), token.getExpiresIn(), serializedToken);
        memcachedClient.set(TOKENS_BY_LOGIN + getApprovalKey(authentication), token.getExpiresIn(), serializedToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return getToken(memcachedClient.get(ACCESS_TOKEN+tokenValue));
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        memcachedClient.delete(ACCESS_TOKEN + token.getValue());
        memcachedClient.delete(AUTH_TOKEN + token.getValue());
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        memcachedClient.add(REFRESH_TOKEN + refreshToken.getValue(), 0, serialize(refreshToken));
        memcachedClient.add(REFRESH_AUTH_TOKEN + refreshToken.getValue(), 0, serialize(authentication));
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return getRefreshToken(memcachedClient.get(REFRESH_TOKEN + tokenValue));
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return getAuth(memcachedClient.get(REFRESH_AUTH_TOKEN + token.getValue()));
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        memcachedClient.delete(REFRESH_AUTH_TOKEN+token.getValue());
        memcachedClient.delete(REFRESH_TOKEN+token.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        OAuth2Authentication auth = getAuth(memcachedClient.get(REFRESH_AUTH_TOKEN+refreshToken.getValue()));
        if (auth != null) {
            memcachedClient.delete(TOKEN_BY_AUTH+authenticationKeyGenerator.extractKey(auth));
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return getToken(memcachedClient.get(TOKEN_BY_AUTH + authenticationKeyGenerator.extractKey(authentication)));
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> tokens = new ArrayList<>(1);
        OAuth2AccessToken token = getToken(memcachedClient.get(TOKENS_BY_LOGIN+getApprovalKey(clientId, userName)));
        if (token != null) {
            tokens.add(token);
        }
        return Collections.unmodifiableList(tokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> tokens = new ArrayList<>(1);
        OAuth2AccessToken token = getToken(memcachedClient.get(TOKENS_BY_ID + clientId));
        if (token != null) {
            tokens.add(token);
        }
        return Collections.unmodifiableList(tokens);
    }


    private String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication()
                .getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private String getApprovalKey(String clientId, String userName) {
        return clientId + (userName==null ? "" : ":" + userName);
    }

    private String serialize(OAuth2AccessToken token) {
        if (token == null) return null;

        String s = null;
        try {
            s = converter.getObjectMapper().writerFor(OAuth2AccessToken.class).writeValueAsString(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.debug("Serializing Token: {}", s);
        return s;
    }

    private String serialize(OAuth2RefreshToken token) {
        if (token == null) return null;

        String s = Base64.getEncoder().encodeToString(SerializationUtils.serialize(token));
        log.debug("Serializing Refresh: {}", s);
        return s;
    }

    private String serialize(OAuth2Authentication token) {
        if (token == null) return null;

        String s = Base64.getEncoder().encodeToString(SerializationUtils.serialize(token));
        log.debug("Serializing Auth: {}", s);

        return s;
    }

    private OAuth2AccessToken getToken(Object token) {
        log.debug("Deserializing Token: {}", (String)token);
        if (token == null) return null;

        try {
            return converter.getObjectMapper().readerFor(OAuth2AccessToken.class).readValue((String)token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private OAuth2RefreshToken getRefreshToken(Object token) {
        log.debug("Deserializing Refresh: {}", (String)token);
        if (token == null) return null;

        return SerializationUtils.deserialize(Base64.getDecoder().decode((String) token));
    }

    private OAuth2Authentication getAuth(Object token) {
        log.debug("Deserializing Auth: {}", (String) token);
        if (token == null) return null;

        OAuth2Authentication req = SerializationUtils.deserialize(Base64.getDecoder().decode((String) token));
        return req;
    }
}
