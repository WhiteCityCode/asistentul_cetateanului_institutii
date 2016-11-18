package com.govac.institutii.db;

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

    public String password;
    public String username;
    public String email;

    public User(String name, String password, String email) {
        this.username = name;
        this.password = password;
        this.email = email;
    }

    User() { // jpa only
    }
}
