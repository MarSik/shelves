package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.NamedObjectToEmber;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/codes")
public class CodeController extends AbstractRestController<Code, CodeApiModel, CodeService> {
    @Autowired
    NamedEntityRepository namedEntityRepository;

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    public CodeController(CodeService service) {
        super(CodeApiModel.class, service);
    }

    @RequestMapping(value = "{type}/{code}", method = RequestMethod.GET)
    public EmberModel getByTypeAndCode(@CurrentUser User user,
                              @PathVariable("code") String code,
                              @PathVariable("type") String type) throws PermissionDenied, EntityNotFound {
        CodeApiModel c = getService().getByTypeAndCode(type, code, user);
        EmberModel.Builder<CodeApiModel> builder = new EmberModel.Builder<CodeApiModel>(c);
        return builder.build();
    }
}
