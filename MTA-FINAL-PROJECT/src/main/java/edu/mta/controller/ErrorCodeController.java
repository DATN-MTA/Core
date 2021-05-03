/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.controller;

import edu.mta.model.ErrorCode;
import edu.mta.service.ErrorCodeService;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/errors")
public class ErrorCodeController {

    @Autowired
    private ErrorCodeService errorCodeService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/saveOrUpdate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//

            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public void createOrUpdate(@RequestBody ErrorCode errorCode) {
        errorCodeService.save(errorCode);
    }

    @DeleteMapping(value = "/{errorCodeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//

            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})

    public void delete(@ApiParam("errorCodeId") @PathVariable Long id) {
        errorCodeService.delete(id);
    }
}
