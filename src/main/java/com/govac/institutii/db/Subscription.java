package com.govac.institutii.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
    
    @ManyToOne
    @JoinColumn(name = "application_id")
    public Application app;

    public Long subscribedOn;

    public Subscription(User user, Application app, Long subscribedOn) {
        this.user = user;
        this.app = app;
        this.subscribedOn = subscribedOn;
    }

    Subscription() { // jpa only
    }
}
