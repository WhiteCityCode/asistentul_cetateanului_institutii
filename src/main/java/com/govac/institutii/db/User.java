package com.govac.institutii.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String cnp;
    public String email;    
    public String phone;
    
    @Column(name = "first_name")
    public String firstName;
    
    @Column(name = "last_name")
    public String lastName;

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
