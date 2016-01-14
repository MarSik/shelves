package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
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
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController extends AbstractRestController<User, UserApiModel, UserService> {
    @Autowired
    MailService mailService;

	ElshelvesUserDetailsService userDetailsService;

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
}
