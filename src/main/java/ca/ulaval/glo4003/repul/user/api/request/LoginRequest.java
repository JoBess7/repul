package ca.ulaval.glo4003.repul.user.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    @NotNull(message = "The email may not be null.")
    public String email;

    @NotNull(message = "The password may not be null.")
    public String password;
}
