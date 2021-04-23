/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.mapper;

import edu.mta.dto.AccountDataDTO;
import edu.mta.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDTOMapper {

   public Account createFrom(final AccountDataDTO dto) {
       Account account = new Account();
       account.setEmail(dto.getEmail());
       account.setUsername(dto.getUsername());
       account.setPassword(dto.getPassword());
       account.setRoles(dto.getRoles());
       return account;
   }
}