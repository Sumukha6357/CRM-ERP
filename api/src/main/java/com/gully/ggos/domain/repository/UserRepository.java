package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndOrg_Id(String email, UUID orgId);
    Optional<User> findFirstByEmailIgnoreCase(String email);

    @org.springframework.data.jpa.repository.Query("""
        select u from User u
        where u.org.id = :orgId
          and (:q is null or lower(u.email) like lower(concat('%', :q, '%'))
               or lower(u.fullName) like lower(concat('%', :q, '%')))
        """)
    org.springframework.data.domain.Page<User> searchByOrg(
        @org.springframework.data.repository.query.Param("orgId") UUID orgId,
        @org.springframework.data.repository.query.Param("q") String q,
        org.springframework.data.domain.Pageable pageable
    );
}
