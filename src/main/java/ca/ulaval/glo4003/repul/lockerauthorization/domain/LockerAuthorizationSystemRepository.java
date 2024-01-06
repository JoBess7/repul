package ca.ulaval.glo4003.repul.lockerauthorization.domain;

public interface LockerAuthorizationSystemRepository {
    void save(LockerAuthorizationSystem deliverySystem);

    LockerAuthorizationSystem get();
}
