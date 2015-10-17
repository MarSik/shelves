package org.marsik.elshelves.backend.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Stale data, please refresh and try again.")
public class UpdateConflict extends BaseRestException {
}
