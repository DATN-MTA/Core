/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.initialize;

import edu.mta.model.ErrorCode;
import edu.mta.repository.ErrorCodeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheInit {

    @Autowired
    private ErrorCodeRepository errorCodeRepository;

    private static Map<String, Map<String, String>> errorMap = new ConcurrentHashMap<>();

    private static List<String> lstErrorCode = new ArrayList();

    private static final Logger logger = Logger.getLogger(CacheInit.class);

    @PostConstruct
    private void loadError() {
        List<ErrorCode> listErrorCode = errorCodeRepository.findByStatus(1L);
        if (listErrorCode != null && !listErrorCode.isEmpty()) {
            errorMap = new ConcurrentHashMap<>();
            lstErrorCode = new ArrayList();
            int size = listErrorCode.size();
            for (int i = 0; i < size; i++) {
                ErrorCode errorBO = listErrorCode.get(i);
                lstErrorCode.add(errorBO.getErrorCode());
                if (errorMap.get(errorBO.getErrorType()) == null) {
                    errorMap.put(errorBO.getErrorType(), new HashMap<>());
                }
                errorMap.get(errorBO.getErrorType()).put(errorBO.getErrorCode(), errorBO.getDescriptionVi());
            }
        }
        
        logger.info("loadError success");

    }
    
    private Map<String, Map<String, String>> getErrorMap() {
        try {
            if (errorMap == null || errorMap.isEmpty()) {
                logger.info("Error map is null: Reload Error data");
                loadError();
            }
        } catch (Exception e) {
            logger.error("getErrorMap have error: ", e);
        }
        return errorMap;
    }
    
    public String getErrorDescription(String errorType, String code) {
        Map<String, Map<String, String>> errorMapT = getErrorMap();
        if (errorMapT == null) {
            return "";
        }
        Map<String, String> map = errorMapT.get(errorType);
        if (map != null && !map.isEmpty()) {
            return map.get(code);
        } else {
            return "";
        }

    }

}
