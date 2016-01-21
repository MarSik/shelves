package org.marsik.elshelves.backend.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.services.BaseOauthService;
import org.marsik.elshelves.backend.services.GithubOauthService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.marsik.elshelves.backend.services.MailService;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController extends AbstractRestController<User, UserApiModel, UserService> {
    @Autowired
    MailService mailService;

	ElshelvesUserDetailsService userDetailsService;

    @Autowired
    GithubOauthService githubOauthService;

    @Autowired
    GoogleOauthService googleOauthService;

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    @Autowired
    public UserController(ElshelvesUserDetailsService service, UserService userService,
                          UserToEmber dbToRest,
                          EmberToUser restToDb) {
        super(UserApiModel.class, userService, dbToRest, restToDb);
		this.userDetailsService = service;
    }

    @Transactional
    @RequestMapping(value = "/verify/{code}", method = RequestMethod.POST)
    public EmberModel verifyUser(@PathVariable("code") String code) throws InvalidRequest, PermissionDenied {
		if (code == null) {
			throw new InvalidRequest();
		}

        UserApiModel user = userDetailsService.verifyUser(code);
        return new EmberModel.Builder<UserApiModel>(user).build();
    }

	@Transactional
	@RequestMapping(value = "/reverify/{email:.+}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void reverify(@PathVariable("email") String email) {
		String verificationCode = userDetailsService.startNewVerification(email);
		if (verificationCode == null) return;

		mailService.sendVerificationCode(email, verificationCode);
	}

    @Transactional
    @RequestMapping("/whoami")
    public EmberModel getCurrentUser(@CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
        final User user = getService().get(currentUser.getId(), currentUser);
        return new EmberModel.Builder<UserApiModel>(getDbToRest().convert(user, new THashMap<>())).build();
    }

    @Override
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EmberModel> create(@CurrentUser User currentUser, @Valid @RequestBody UserApiModel user, HttpServletRequest request) throws OperationNotPermitted {
        String verificationCode = userDetailsService.createUser(user);
        mailService.sendVerificationCode(user.getEmail(), verificationCode);

        return ResponseEntity
                .ok()
                .body(new EmberModel.Builder<UserApiModel>(user).build());
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EmberModel> getAll(@CurrentUser User currentUser,
                                             @RequestParam(value = "ids[]", required = false) UUID[] ids,
                                             @RequestParam(value = "include", required = false) String include) throws BaseRestException {
        if (ids == null) {
            throw new PermissionDenied();
        }

        for (UUID id: ids) {
            if (!id.equals(currentUser.getId())) {
                throw new PermissionDenied();
            }
        }
        
        return super.getAll(currentUser, ids, include);
    }

    /**
     * Perform federated login association to an existing user
     *
     * @param currentUser logged in user
     * @param oauthRule base64 encoded json with grant_type and necessary other fields
     *
     * @throws BaseRestException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @RequestMapping(value = "/associate", method = RequestMethod.POST)
    public ResponseEntity<Void> associateExternalId(@CurrentUser User currentUser,
            @RequestParam("oauth") String oauthRule) throws BaseRestException, IOException, GeneralSecurityException {
        String jAuth;

        try {
            jAuth = new String(Base64.getDecoder().decode(oauthRule), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        Map<String, String> auth = converter.getObjectMapper().readValue(jAuth, new TypeReference<Map<String, String>>() {});

        BaseOauthService service = null;

        final String grant_type = auth.get("grant_type");
        if ("google".equals(grant_type)) {
            service = googleOauthService;
        } else if ("github".equals(grant_type)) {
            service = githubOauthService;
        }

        if (service == null) {
            throw new IllegalArgumentException("Unrecognized grant type " + grant_type);
        }

        service.getOrRegisterUser(currentUser, auth.get("code"), auth.get("state"));
        return ResponseEntity.ok(null);
    }
}
