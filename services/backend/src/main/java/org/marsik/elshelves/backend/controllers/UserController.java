package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.MailgunService;
import org.marsik.elshelves.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractRestController<User, UserApiModel, UserService> {
    @Autowired
    MailgunService mailgunService;

	ElshelvesUserDetailsService userDetailsService;

    @Autowired
    public UserController(ElshelvesUserDetailsService service, UserService userService) {
        super(UserApiModel.class, userService);
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

		mailgunService.sendVerificationCode(email, verificationCode);
	}

    @Transactional
    @RequestMapping("/whoami")
    public EmberModel getCurrentUser(@CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
        return new EmberModel.Builder<UserApiModel>(getService().get(currentUser.getUuid(), currentUser)).build();
    }

    @Override
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public EmberModel create(@CurrentUser User currentUser, @Valid @RequestBody UserApiModel user) throws OperationNotPermitted {
        String verificationCode = userDetailsService.createUser(user);
        mailgunService.sendVerificationCode(user.getEmail(), verificationCode);

        return new EmberModel.Builder<UserApiModel>(user).build();
    }
}
