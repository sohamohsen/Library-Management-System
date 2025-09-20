package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MembersRepository
        extends JpaRepository<Members, Integer>, JpaSpecificationExecutor<Members> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);
    boolean existsByPhoneAndIdNot(String phone, Integer id);
}