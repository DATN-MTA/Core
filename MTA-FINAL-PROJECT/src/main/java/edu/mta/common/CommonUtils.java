/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.common;

import java.util.List;
import org.apache.log4j.Logger;
import org.apache.commons.collections.CollectionUtils;

public class CommonUtils {
    
    public static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }
    
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    
    public static void warningProcessTime(Logger logger,Long processTime, List<Integer> threshold, String step) {
        if (CollectionUtils.isEmpty(threshold)) return;

        int thresholdLevel = 0;
        for (int t : threshold) {
            if (processTime >= t) thresholdLevel++;
        }
        if(thresholdLevel > 0)
            logger.warn("!!! Waring process invoice time -> level " + thresholdLevel + ": Process of step " + step + " is " + processTime +
                    " miliseconds. Current threshold " + threshold);
    }
    
}