package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.UUID;

@Service
public class TypeService extends AbstractRestService<TypeRepository, Type> {
	@Autowired
	FootprintRepository footprintRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	public TypeService(TypeRepository repository,
					   UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Type> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    public Type getUniqueTypeByNameAndFootprint(String name, String footprint, User currentUser) throws PermissionDenied {
        /* If there is only one type matching the description, save a reference to it */
        Iterable<Type> res = getRepository().findByNameAndFootprintName(name, footprint, currentUser);
        Type ret = null;

        Iterator<Type> iterator = res.iterator();
        if (iterator.hasNext()) {
            Type t = iterator.next();

            if (!iterator.hasNext()) {
                ret = t;
            }
        }

        return ret;
    }
}
