/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.repository;

import edu.mta.model.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer>{
//    Optional<Course> findByStatus(String courseName);
    List<ErrorCode> findByStatus (Long status);
    
    Optional<ErrorCode> findById (Long id);
    
    boolean existsByErrorCode(String errorCode);
}
