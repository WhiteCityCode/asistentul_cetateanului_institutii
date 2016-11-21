package com.govac.institutii.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public String profile;

    public UserProfile(String profile, User user) {
        this.profile = profile;
        this.user = user;
    }

    UserProfile() { // jpa only
    }
}
