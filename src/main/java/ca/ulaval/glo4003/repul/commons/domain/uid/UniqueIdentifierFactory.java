package ca.ulaval.glo4003.repul.commons.domain.uid;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidUniqueIdentifierException;

public class UniqueIdentifierFactory<T extends UniqueIdentifier> {
    private final Class<T> clazz;

    public UniqueIdentifierFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T generate() {
        return create(UUID.randomUUID());
    }

    public T generateFrom(String id) {
        try {
            return create(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new InvalidUniqueIdentifierException();
        }
    }

    public T generateFrom(UniqueIdentifier uniqueIdentifier) {
        return create(uniqueIdentifier.getUUID());
    }

    public List<T> generateFrom(List<String> ids) {
        return ids.stream().map(this::generateFrom).toList();
    }

    private T create(UUID uuid) {
        try {
            return clazz.getDeclaredConstructor(UUID.class).newInstance(uuid);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No constructor with UUID parameter found in " + clazz.getName());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not instantiate " + clazz.getName());
        }
    }
}
