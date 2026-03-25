package com.example.asm1.Entity;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterForm {

    @NotBlank(message = "Required")
    private String email;

    @NotBlank(message = "Required")
    private String code;

    @NotBlank(message = "Required")
    private String firstName;

    @NotBlank(message = "Required")
    private String surname;

    @NotBlank(message = "Required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Minimum of 8 characters, uppercase, lowercase letters and one number"
    )
    private String password;

    @NotBlank(message = "Please select a preference")
    private String shoppingPreference;

    @NotNull
    private Integer dobDay;

    @NotNull
    private Integer dobMonth;

    @NotNull
    private Integer dobYear;

    private boolean emailSignup;

    @AssertTrue(message = "You must agree to the terms")
    private boolean agreeTerms;
}
