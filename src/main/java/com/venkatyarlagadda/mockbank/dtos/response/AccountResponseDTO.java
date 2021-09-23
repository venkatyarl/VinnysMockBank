package com.venkatyarlagadda.mockbank.dtos.response;

import com.venkatyarlagadda.mockbank.entity.Account;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Builder
@Data
public class AccountResponseDTO implements Serializable {

    private UUID accountNumber;
    private BigDecimal accountBalance;
    private UUID customerNumber;

    public static AccountResponseDTO buildAccountResponseDTO(final Account account){
        return AccountResponseDTO
                .builder()
                .accountNumber(account.getAccountNumber())
                .accountBalance(account.getAccountBalance())
                .customerNumber(account.getCustomer().getCustomerNumber())
                .build();
    }

    public static List<AccountResponseDTO> buildAccountResponseDTO(final List<Account> listOfAccounts){
        return listOfAccounts
                .stream()
                .map(AccountResponseDTO::buildAccountResponseDTO)
                .collect(Collectors.toList());
    }

}
