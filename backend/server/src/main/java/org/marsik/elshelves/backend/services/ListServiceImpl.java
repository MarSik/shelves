package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.List;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.ListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListServiceImpl extends AbstractRestService<ListRepository, List> implements ListService {
    @Autowired
    public ListServiceImpl(ListRepository repository, UuidGenerator uuidGenerator) {
        super(repository, uuidGenerator);
    }

    @Override
    protected Iterable<List> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
