package ca.ulaval.glo4003.repul.delivery.domain;

public interface DeliverySystemPersister {
    void save(DeliverySystem deliverySystem);

    DeliverySystem get();
}
