package com.govac.institutii.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository 
        extends PagingAndSortingRepository<Application, Long>{
    @Query("SELECT app FROM Application AS app LEFT JOIN app.provider AS p "
            + "LEFT JOIN p.admin AS adm WHERE adm.email = :user_email")
    Page<Application> findByAdminEmail(
            @Param("user_email") String email, 
            Pageable pageable
    );
}
