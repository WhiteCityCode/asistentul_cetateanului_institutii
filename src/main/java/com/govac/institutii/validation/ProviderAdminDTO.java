package com.govac.institutii.validation;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class ProviderAdminDTO {
    public Long admin;

    @NotBlank(message = "error.provider.name.notblank")
    public String name;
    
    @NotBlank(message = "error.provider.url.notblank")
    @URL(message = "error.provider.url.url")
    public String url;

    public ProviderAdminDTO(Long admin, String name, String url) {
        this.admin = admin;
        this.name = name;
        this.url = url;
    }
    
    public ProviderAdminDTO() {
        
    }
}
