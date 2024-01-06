package ca.ulaval.glo4003.repul.subscription.application.payload;

import ca.ulaval.glo4003.repul.subscription.domain.profile.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfilePayload(
    String name,
    String birthdate,
    String gender,
    int age,
    String idul,
    String email,
    String cardNumber
) {
    public static ProfilePayload from(Profile profile) {
        return new ProfilePayload(profile.getName().value(), profile.getBirthdate().value().toString(), profile.getGender().name(), profile.getAge(),
            profile.getIdul().value(), profile.getEmail().value(), profile.getCardNumber().isPresent() ? profile.getCardNumber().get().value() : null);
    }
}
