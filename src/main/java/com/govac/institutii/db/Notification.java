package com.govac.institutii.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "application_id")
    public Application app;

    public String title;
    public String description;
    public String metadata;
    
    @Column(name="short_description")
    public String shortDescription;

    public Notification(Application app, String title, String desc, String shortDesc, String meta) {
        this.app = app;
        this.title = title;
        this.description = desc;
        this.shortDescription = shortDesc;
        this.metadata = meta;
    }

    Notification() { // jpa only
    }
}
