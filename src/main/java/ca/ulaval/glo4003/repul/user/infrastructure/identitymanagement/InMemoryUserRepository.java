package ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement;

import java.util.HashMap;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.UserNotFoundException;

public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Email, User> users = new HashMap<>();

    @Override
    public User findByEmail(Email email) {
        return Optional.ofNullable(users.get(email)).orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public User findByUid(UniqueIdentifier uid) {
        return users.values().stream().filter(user -> user.getUid().equals(uid)).findFirst().orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public boolean exists(Email email) {
        return users.containsKey(email);
    }

    @Override
    public boolean exists(IDUL idul) {
        return users.values().stream().anyMatch(user -> user.getIdul().equals(idul));
    }

    @Override
    public void save(User user) {
        users.put(user.getEmail(), user);
    }
}
