package com.govac.institutii.db;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "error.user.cnp.notblank")
    @Size(min = 13, max = 13, message = "error.user.cnp.size")
    public String cnp;

    @NotBlank(message = "error.user.email.notblank")
    @Email(message = "error.user.email.email")
    public String email;

    @NotBlank(message = "error.user.phone.notblank")
    public String phone;
    
    @JsonIgnore
    public String role;

    @Column(name = "first_name")
    @NotBlank(message = "error.user.firstname.notblank")
    public String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "error.user.lastname.notblank")
    public String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User(String cnp, String email, String firstName, String lastName, String phone) {
        this.cnp = cnp;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    User() { // jpa only
    }
}
