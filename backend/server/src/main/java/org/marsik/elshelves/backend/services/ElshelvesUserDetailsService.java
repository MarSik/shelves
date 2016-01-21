package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ElshelvesUserDetailsService extends UserDetailsService {
    String createUser(UserApiModel userInfo) throws OperationNotPermitted;
    UserApiModel verifyUser(String code) throws PermissionDenied;
    User getUser(String email);
	public String startNewVerification(String email);
    User createOrAttachUser(String name, String email, String externalId);
    User attachUser(User user, String externalId);
}
