package org.marsik.elshelves.backend.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.dtos.UserInfo;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.api.dtos.LotMetric;
import org.marsik.elshelves.backend.repositories.results.MoneySum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractRestService<UserRepository, User> implements UserService {
	@Autowired
	public UserServiceImpl(UserRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	public User create(User dto, User currentUser) throws OperationNotPermitted {
		throw new OperationNotPermitted();
	}

    @Override
    protected Iterable<User> getAllEntities(User currentUser) {
        /* TODO XXX check if the currentUser has admin rights... */

        return getRepository().findAll();
    }

	@Override
	public UserInfo getInfo(UUID id, User currentUser) {
		List<MoneySum> moneyPaid = repository.totalPricePaid(currentUser);
		LotMetric lotMetric = repository.lotCount(currentUser);
		Map<String, Double> money = moneyPaid.stream()
				.collect(Collectors.groupingBy(
						MoneySum::getCurrency,
						Collectors.summingDouble(s -> s.getAmount().doubleValue())));

		return new UserInfo(money, lotMetric);
	}
}
