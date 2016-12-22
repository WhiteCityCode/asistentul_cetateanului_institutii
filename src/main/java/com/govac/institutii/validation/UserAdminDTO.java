package com.govac.institutii.validation;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class UserAdminDTO {
    @NotBlank(message = "error.user.cnp.notblank")
    @Size(min = 13, max = 13, message = "error.user.cnp.size")
    public String cnp;

    @NotBlank(message = "error.user.email.notblank")
    @Email(message = "error.user.email.email")
    public String email;

    @NotBlank(message = "error.user.phone.notblank")
    public String phone;

    @NotBlank(message = "error.user.firstname.notblank")
    public String firstName;

    @NotBlank(message = "error.user.lastname.notblank")
    public String lastName;
    
    @Pattern(regexp="^ROLE\\_ADMIN|ROLE\\_PROVIDER$", message = "error.user.role.regex")
    public String role;
}
