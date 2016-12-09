package com.govac.institutii.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "providers")
public class Provider implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    public User admin;

    @NotBlank(message = "error.provider.name.notblank")
    public String name;
    
    @NotBlank(message = "error.provider.url.notblank")
    @URL(message = "error.provider.url.url")
    public String url;

    public Provider(User admin, String name, String url) {
        this.admin = admin;
        this.name = name;
        this.url = url;
    }

    public Provider() { // jpa only
    }
}
