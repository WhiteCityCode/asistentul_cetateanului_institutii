package com.govac.institutii.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "provider_id")
    public Provider provider;

    public String name;
    public String description;
    public String tkn;
    public String requirements;

    public Application(Provider provider, String name, String desc, String tkn, String reqs) {
        this.provider = provider;
        this.name = name;
        this.description = desc;
        this.tkn = tkn;
        this.requirements = reqs;
    }

    Application() { // jpa only
    }
}
