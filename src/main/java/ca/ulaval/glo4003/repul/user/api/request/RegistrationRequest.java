package ca.ulaval.glo4003.repul.user.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {
    @NotNull(message = "The IDUL may not be null.")
    public String idul;

    @NotNull(message = "The email may not be null.")
    public String email;

    @NotNull(message = "The password may not be null.")
    public String password;

    @NotNull(message = "The name may not be null.")
    public String name;

    @NotNull(message = "The birthdate may not be null.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The given date is in an invalid date format. Use yyyy-MM-dd.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public String birthdate;

    @NotNull(message = "The gender may not be null.")
    public String gender;
}
