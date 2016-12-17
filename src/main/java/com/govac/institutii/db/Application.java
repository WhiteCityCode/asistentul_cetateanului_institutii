package com.govac.institutii.db;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "applications")
@TypeDef(name = "jsonb", typeClass = JSONBUserType.class, parameters = {
    @Parameter(name = JSONBUserType.CLASS, value = "java.util.List")})
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
    @Type(type = "jsonb")
    public List<String> requirements;

    public Application(
            Provider provider, String name, String desc, 
            String tkn, List<String> reqs
    ) {
        this.provider = provider;
        this.name = name;
        this.description = desc;
        this.tkn = tkn;
        this.requirements = reqs;
    }

    Application() { // jpa only
    }
}
