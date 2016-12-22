package com.govac.institutii.validation;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class ApplicationAdminDTO {
    
    @NotNull(message = "error.application.provider.notnull")
    public Long provider;
    
    @NotBlank(message = "error.application.name.notblank")
    public String name;
    
    @NotBlank(message = "error.application.description.notblank")
    public String description;
    
    @NotNull(message = "error.application.requirements.notnull")
    public List<String> requirements;

    public ApplicationAdminDTO(
            Long provider, String name, String description, 
            List<String> requirements
    ) {
        this.provider = provider;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
    }
    
    public ApplicationAdminDTO() {
        
    }
}
