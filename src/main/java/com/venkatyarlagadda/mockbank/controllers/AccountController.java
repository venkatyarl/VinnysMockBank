package com.venkatyarlagadda.mockbank.controllers;

import com.venkatyarlagadda.mockbank.dtos.request.AccountRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.AccountTransferRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.response.AccountResponseDTO;
import com.venkatyarlagadda.mockbank.entity.Account;
import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;
import com.venkatyarlagadda.mockbank.repositories.AccountRepository;
import com.venkatyarlagadda.mockbank.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Component
public class AccountController {
    private final AccountRepository accountRepository;
    private final CustomerController customerController;

    public AccountController(final AccountRepository accountRepository,
                             final CustomerController customerController) {
        this.accountRepository = accountRepository;
        this.customerController = customerController;
    }

    public ResponseEntity<List<AccountResponseDTO>> getAccounts(final String customerNumber)
            throws InvalidUUIDException {
        final List<AccountResponseDTO> result = AccountResponseDTO
                .buildAccountResponseDTO(accountRepository.findByCustomer_CustomerNumber(
                        StringUtils.convertStringToUUID(customerNumber)));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<AccountResponseDTO> createAccount(final AccountRequestDTO accountRequestDTO,
                                                            final String customerNumber)
            throws InvalidUUIDException {
        return customerController.getCustomerWithCustomerNumber(StringUtils.convertStringToUUID(customerNumber))
                .map(customer -> {
                    final Account account = accountRepository.save(Account.buildAccount(accountRequestDTO, customer));
                    final AccountResponseDTO result = AccountResponseDTO.buildAccountResponseDTO(account);
                    return new ResponseEntity<>(result, HttpStatus.CREATED);
                }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<Boolean> deleteAccount(final String customerNumber, final String accountNumber)
            throws InvalidUUIDException {
        return accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        StringUtils.convertStringToUUID(accountNumber),
                        StringUtils.convertStringToUUID(customerNumber))
                .map(account -> {
                    if (account.getAccountBalance().compareTo(BigDecimal.ZERO) == 0) {
                        accountRepository.delete(account);
                        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.ACCEPTED);
                    } else {
                        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
                    }
                }).orElseGet(() -> new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<AccountResponseDTO> depositIntoAccount(final String customerNumber,
                                                                 final String accountNumber,
                                                                 final AccountRequestDTO accountRequestDTO)
            throws InvalidUUIDException {
        return getAccountByAccountNumberAndCustomerNumber(customerNumber, accountNumber)
                .map(account -> {
                    account.setAccountBalance(account.getAccountBalance().add(
                            new BigDecimal(accountRequestDTO.getAmount())));
                    accountRepository.save(account);
                    final AccountResponseDTO result = AccountResponseDTO.buildAccountResponseDTO(account);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<AccountResponseDTO> withdrawFromAccount(final String customerNumber,
                                                                  final String accountNumber,
                                                                  final AccountRequestDTO accountRequestDTO)
            throws InvalidUUIDException {
        return getAccountByAccountNumberAndCustomerNumber(customerNumber, accountNumber)
                .map(account -> {
                    if (account.getAccountBalance().compareTo(new BigDecimal(accountRequestDTO.getAmount())) >= 0) {
                        account.setAccountBalance(account.getAccountBalance().subtract(
                                new BigDecimal(accountRequestDTO.getAmount())));
                        accountRepository.save(account);
                        final AccountResponseDTO result = AccountResponseDTO.buildAccountResponseDTO(account);
                        return new ResponseEntity<>(result, HttpStatus.OK);
                    }
                    return new ResponseEntity<>(AccountResponseDTO.buildAccountResponseDTO(account),
                            HttpStatus.BAD_REQUEST);
                }).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<List<AccountResponseDTO>> transferAccount(
            final AccountTransferRequestDTO accountTransferRequestDTO) {
        try {
            final Optional<Account> optionalFromAccount =
                    getAccountByAccountNumberAndCustomerNumber(
                            accountTransferRequestDTO.getCustomerNumber(),
                            accountTransferRequestDTO.getFromAccount());
            final Optional<Account> optionalToAccount =
                    getAccountByAccountNumberAndCustomerNumber(
                            accountTransferRequestDTO.getCustomerNumber(),
                            accountTransferRequestDTO.getToAccount());
            final BigDecimal transferAmount = new BigDecimal(accountTransferRequestDTO.getTransferAmount());

            if (optionalFromAccount.isPresent()
                    && optionalFromAccount.get().getAccountBalance().compareTo(transferAmount) >= 0
                    && optionalToAccount.isPresent()
                    && !optionalFromAccount.get().getAccountNumber().toString().equalsIgnoreCase(
                    optionalToAccount.get().getAccountNumber().toString()
            )) {

                final Account fromAccount = optionalFromAccount.get();
                final Account toAccount = optionalToAccount.get();

                fromAccount.setAccountBalance(fromAccount.getAccountBalance().subtract(transferAmount));
                toAccount.setAccountBalance(toAccount.getAccountBalance().add(transferAmount));

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                return getAccounts(accountTransferRequestDTO.getCustomerNumber());
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (InvalidUUIDException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    private Optional<Account> getAccountByAccountNumberAndCustomerNumber(final String customerNumber,
                                                                         final String accountNumber)
            throws InvalidUUIDException {
        return accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                StringUtils.convertStringToUUID(accountNumber),
                StringUtils.convertStringToUUID(customerNumber));

    }
}
