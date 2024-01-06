package ca.ulaval.glo4003.repul.cooking.domain;

public interface KitchenPersister {
    Kitchen get();

    void save(Kitchen kitchen);
}
