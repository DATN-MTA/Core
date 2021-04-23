/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.service;

import edu.mta.model.ErrorCode;
import java.io.IOException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ErrorCodeService {
    
    ErrorCode save(ErrorCode errorCode);
    Page<ErrorCode> findAll(Pageable pageable);
    Optional<ErrorCode> findOne(Long id);
    void delete(Long id);

    ErrorCode create(ErrorCode errorCode) throws IOException;
    
}
