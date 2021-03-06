package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CodeToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToCode;
import org.marsik.elshelves.backend.entities.converters.NamedObjectToEmber;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/codes")
public class CodeController extends AbstractRestController<Code, CodeApiModel, CodeService> {
    @Autowired
    NamedEntityRepository namedEntityRepository;

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    public CodeController(CodeService service,
                          CodeToEmber dbToRest,
                          EmberToCode restToDb) {
        super(CodeApiModel.class, service, dbToRest, restToDb);
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/{type}/{code}", method = RequestMethod.GET)
    public EmberModel getByTypeAndCode(@CurrentUser User user,
                                       @PathVariable("code") String code,
                                       @PathVariable("type") String type,
                                       @RequestParam(value = "include", required = false) String include) throws PermissionDenied, EntityNotFound {
        Code c = getService().getByTypeAndCode(type, code, user);
        Map<UUID, Object> cache = new THashMap<>();
        CodeApiModel res = getDbToRest().convert(null, null, c, cache, processInclude(include));
        EmberModel.Builder<CodeApiModel> builder = new EmberModel.Builder<>(res);
        cache.remove(res.getId());
        builder.sideLoad(cache.values());
        return builder.build();
    }
}
