package org.japector.shopping.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LoginDto {
    @Email(message = "Please provide a valid e-mail address.")
    private String email;
    @NotNull
    @NotEmpty(message = "Please provide a password")
    private String password;

}
