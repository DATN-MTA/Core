/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.service;

import edu.mta.exception.CustomException;
import edu.mta.initialize.CacheInit;
import edu.mta.model.ErrorCode;
import edu.mta.repository.ErrorCodeRepository;
import java.io.IOException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author vtt-thangnh21-bk
 */

@Service
@Transactional
public class ErrorCodeServiceImpl implements ErrorCodeService{
    
    @Autowired
    private CacheInit cacheInit;
    
    @Autowired
    private ErrorCodeRepository errorCodeRepository;

    @Override
    public ErrorCode save(ErrorCode errorCode) {
        if (!errorCodeRepository.existsByErrorCode(errorCode.getErrorCode())) {
            return errorCodeRepository.save(errorCode);
        } else {
            throw new CustomException(cacheInit.getErrorDescription("core", "402"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
    }

    @Override
    public Page<ErrorCode> findAll(Pageable pageable) {
        return errorCodeRepository.findAll(pageable);
    }

    @Override
    public Optional<ErrorCode> findOne(Long id) {
        return errorCodeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        errorCodeRepository.deleteById(id.intValue());
    }

    @Override
    public ErrorCode create(ErrorCode errorCode) throws IOException {
        return errorCodeRepository.save(errorCode);
    }
    
}
