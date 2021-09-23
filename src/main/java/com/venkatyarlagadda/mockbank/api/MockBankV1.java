package com.venkatyarlagadda.mockbank.api;

import com.venkatyarlagadda.mockbank.controllers.AccountController;
import com.venkatyarlagadda.mockbank.controllers.CustomerController;
import com.venkatyarlagadda.mockbank.dtos.request.AccountRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.AccountTransferRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.response.AccountResponseDTO;
import com.venkatyarlagadda.mockbank.dtos.response.CustomerResponseDTO;
import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@RestController
@RequestMapping(value = "/mockbank/v1")
public class MockBankV1 {

    final private CustomerController customerController;
    final private AccountController accountController;

    public MockBankV1(final CustomerController customerController,
                      final AccountController accountController) {
        this.customerController = customerController;
        this.accountController = accountController;
    }

    @GetMapping(value = "/customers")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers() {
        return customerController.getCustomers();
    }

    @GetMapping(value = "/customers/{customerNumber}")
    public ResponseEntity<CustomerResponseDTO> getCustomerWithCustomerNumber(
            @NotNull @PathVariable final String customerNumber)
            throws InvalidUUIDException {
        return customerController.getCustomerWithCustomerNumber(customerNumber);
    }

    @PutMapping(value = "/customers/{customerNumber}")
    public ResponseEntity<CustomerResponseDTO> updateCustomerDetails(
            @NotNull @PathVariable final String customerNumber,
            @Valid @RequestBody final CustomerDTO customerDTO)
            throws InvalidUUIDException {
        return customerController.updateCustomerDetails(customerDTO, customerNumber);
    }

    @DeleteMapping(value = "/customers/{customerNumber}")
    public boolean deleteCustomerWithCustomerNumber(
            @NotNull @PathVariable final String customerNumber)
            throws InvalidUUIDException {
        return customerController.deleteCustomerWithCustomerNumber(customerNumber);
    }

    @PostMapping(value = "/customer/create")
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody final CustomerRequestDTO customerRequest) {
        return customerController.createCustomer(customerRequest);
    }

    @GetMapping(value = "/customers/{customerNumber}/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getCustomerAccounts(
            @NotNull @PathVariable final String customerNumber)
            throws InvalidUUIDException {
        return accountController.getAccounts(customerNumber);
    }

    @PostMapping(value = "/customers/{customerNumber}/account")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @NotNull @PathVariable final String customerNumber,
            @Valid @RequestBody final AccountRequestDTO accountRequestDTO)
            throws InvalidUUIDException {
        return accountController.createAccount(accountRequestDTO, customerNumber);
    }

    @DeleteMapping(value = "/customers/{customerNumber}/account/{accountNumber}")
    public ResponseEntity<Boolean> deleteAccount(
            @NotNull @PathVariable final String customerNumber,
            @NotNull @PathVariable final String accountNumber)
            throws InvalidUUIDException {
        return accountController.deleteAccount(customerNumber, accountNumber);
    }

    @PostMapping(value = "/customers/{customerNumber}/account/{accountNumber}/deposit")
    public ResponseEntity<AccountResponseDTO> depositIntoAccount(
            @NotNull @PathVariable final String customerNumber,
            @NotNull @PathVariable final String accountNumber,
            @Valid @RequestBody final AccountRequestDTO accountRequestDTO)
            throws InvalidUUIDException {
        return accountController.depositIntoAccount(customerNumber, accountNumber, accountRequestDTO);
    }

    @PostMapping(value = "/customers/{customerNumber}/account/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponseDTO> withdrawFromAccount(
            @NotNull @PathVariable final String customerNumber,
            @NotNull @PathVariable final String accountNumber,
            @Valid @RequestBody final AccountRequestDTO accountRequestDTO)
            throws InvalidUUIDException {
        return accountController.withdrawFromAccount(customerNumber, accountNumber, accountRequestDTO);
    }

    @PostMapping(value = "/transfer")
    public ResponseEntity<List<AccountResponseDTO>> transferAccount(
            @Valid @RequestBody final AccountTransferRequestDTO accountTransferRequestDTO)
            throws InvalidUUIDException {
        return accountController.transferAccount(accountTransferRequestDTO);
    }
}
