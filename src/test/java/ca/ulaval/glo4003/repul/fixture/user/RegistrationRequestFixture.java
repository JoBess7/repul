package ca.ulaval.glo4003.repul.fixture.user;

import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;

public class RegistrationRequestFixture {
    private String idul = "ALMAT69";
    private String email = "anEmail@ulaval.ca";
    private String password = "aPassword";
    private String name = "Bob Math";
    private String birthdate = "1969-04-20";
    private String gender = "MAN";

    public RegistrationRequestFixture withIdul(String idul) {
        this.idul = idul;
        return this;
    }

    public RegistrationRequestFixture withEmail(String email) {
        this.email = email;
        return this;
    }

    public RegistrationRequestFixture withPassword(String password) {
        this.password = password;
        return this;
    }

    public RegistrationRequestFixture withName(String name) {
        this.name = name;
        return this;
    }

    public RegistrationRequestFixture withBirthdate(String birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public RegistrationRequestFixture withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public RegistrationRequest build() {
        RegistrationRequest request = new RegistrationRequest();
        request.idul = idul;
        request.email = email;
        request.password = password;
        request.name = name;
        request.birthdate = birthdate;
        request.gender = gender;

        return request;
    }
}
