package org.japector.shopping.model;

import java.util.StringJoiner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {
    @NotNull
    @NotEmpty(message = "Please enter your first name.")
    private String firstName;
    @NotNull
    @NotEmpty(message = "Please enter your last name.")
    private String lastName;
    @Email(message = "Please provide a valid e-mail address.")
    private String email;
    @NotNull
    @NotEmpty(message = "Please provide a password")
    private String password;


    @Override
    public String toString() {
        return new StringJoiner(", ", UserDto.class.getSimpleName() + "[", "]")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .toString();
    }
}
