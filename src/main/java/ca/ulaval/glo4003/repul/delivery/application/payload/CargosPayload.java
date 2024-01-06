package ca.ulaval.glo4003.repul.delivery.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;

public record CargosPayload(
    List<CargoPayload> cargoPayloads
) {
    public static CargosPayload from(List<Cargo> cargos) {
        return new CargosPayload(cargos.stream().map(CargoPayload::from).toList());
    }
}
