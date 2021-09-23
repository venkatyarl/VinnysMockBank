package com.venkatyarlagadda.mockbank.controllers;

import com.venkatyarlagadda.mockbank.dtos.request.AccountRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.AccountTransferRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.response.AccountResponseDTO;
import com.venkatyarlagadda.mockbank.entity.Account;
import com.venkatyarlagadda.mockbank.entity.Customer;
import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;
import com.venkatyarlagadda.mockbank.repositories.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AccountControllerTest {
    private AccountController accountController;
    private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private CustomerController customerController = Mockito.mock(CustomerController.class);

    @BeforeEach
    void setUp() {
        accountController = new AccountController(accountRepository, customerController);
    }

    @Test
    void getAccounts() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final AccountRequestDTO accountRequestDTO2 =
                AccountRequestDTO
                        .builder()
                        .amount("0")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);
        final Account account2 = Account.buildAccount(accountRequestDTO2, customer1);

        Mockito.when(accountRepository.findByCustomer_CustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(List.of(account1, account2));

        final ResponseEntity<List<AccountResponseDTO>> response =
                accountController.getAccounts(customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void createAccount() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        Mockito.when(customerController.getCustomerWithCustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));

        Mockito.when(accountRepository.save(Mockito.any()))
                .thenReturn(account1);

        final ResponseEntity<AccountResponseDTO> response =
                accountController.createAccount(
                        accountRequestDTO1,
                        customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void createAccountFailure() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        Mockito.when(accountRepository.findByCustomer_CustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(List.of(account1));

        final ResponseEntity<AccountResponseDTO> response =
                accountController.createAccount(
                        accountRequestDTO1,
                        customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void deleteAccountSuccess() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO2 =
                AccountRequestDTO
                        .builder()
                        .amount("0")
                        .build();

        final Account account2 = Account.buildAccount(accountRequestDTO2, customer1);

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account2.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account2));

        final ResponseEntity<Boolean> response =
                accountController.deleteAccount(
                        customer1.getCustomerNumber().toString(),
                        account2.getAccountNumber().toString());

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody());
    }

    @Test
    void deleteAccountFailureWithAccountBalance() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<Boolean> response =
                accountController.deleteAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody());
    }

    @Test
    void deleteAccountFailureBadAccountNumber() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        UUID.randomUUID(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<Boolean> response =
                accountController.deleteAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody());
    }

    @Test
    void depositIntoAccountSuccess() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountRequestDTO accountRequestDTO =
                AccountRequestDTO
                        .builder()
                        .amount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<AccountResponseDTO> response =
                accountController.depositIntoAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString(),
                        accountRequestDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("150", response.getBody().getAccountBalance().toString());
    }

    @Test
    void depositIntoAccountCantFindAccount() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountRequestDTO accountRequestDTO =
                AccountRequestDTO
                        .builder()
                        .amount("150")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.empty());

        final ResponseEntity<AccountResponseDTO> response =
                accountController.depositIntoAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString(),
                        accountRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void withdrawFromAccountSuccess() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountRequestDTO accountRequestDTO =
                AccountRequestDTO
                        .builder()
                        .amount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<AccountResponseDTO> response =
                accountController.withdrawFromAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString(),
                        accountRequestDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("50", response.getBody().getAccountBalance().toString());
    }

    @Test
    void withdrawFromAccountInsufficientFunds() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountRequestDTO accountRequestDTO =
                AccountRequestDTO
                        .builder()
                        .amount("150")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<AccountResponseDTO> response =
                accountController.withdrawFromAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString(),
                        accountRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("100", response.getBody().getAccountBalance().toString());
    }

    @Test
    void withdrawFromAccountCantFindAccount() throws InvalidUUIDException {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountRequestDTO accountRequestDTO =
                AccountRequestDTO
                        .builder()
                        .amount("150")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.empty());

        final ResponseEntity<AccountResponseDTO> response =
                accountController.withdrawFromAccount(
                        customer1.getCustomerNumber().toString(),
                        account1.getAccountNumber().toString(),
                        accountRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void transferAccountSuccess() {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final AccountRequestDTO accountRequestDTO2 =
                AccountRequestDTO
                        .builder()
                        .amount("0")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);
        final Account account2 = Account.buildAccount(accountRequestDTO2, customer1);

        final AccountTransferRequestDTO accountTransferRequestDTO =
                AccountTransferRequestDTO
                        .builder()
                        .customerNumber(customer1.getCustomerNumber().toString())
                        .fromAccount(account1.getAccountNumber().toString())
                        .toAccount(account2.getAccountNumber().toString())
                        .transferAmount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account2.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account2));

        final ResponseEntity<List<AccountResponseDTO>> response =
                accountController.transferAccount(accountTransferRequestDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        for (AccountResponseDTO accountResponseDTO : response.getBody()) {
            if (accountResponseDTO.getAccountNumber().toString().equalsIgnoreCase(
                    account1.getAccountNumber().toString())) {
                Assertions.assertEquals("50", accountResponseDTO.getAccountBalance().toString());
            }
            if (accountResponseDTO.getAccountNumber().toString().equalsIgnoreCase(
                    account2.getAccountNumber().toString())) {
                Assertions.assertEquals("50", accountResponseDTO.getAccountBalance().toString());
            }
        }

    }

    @Test
    void transferAccountFailureSameAccount() {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);

        final AccountTransferRequestDTO accountTransferRequestDTO =
                AccountTransferRequestDTO
                        .builder()
                        .customerNumber(customer1.getCustomerNumber().toString())
                        .fromAccount(account1.getAccountNumber().toString())
                        .toAccount(account1.getAccountNumber().toString())
                        .transferAmount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        final ResponseEntity<List<AccountResponseDTO>> response =
                accountController.transferAccount(accountTransferRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void transferAccountFailureCantFindAccount() {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final AccountRequestDTO accountRequestDTO2 =
                AccountRequestDTO
                        .builder()
                        .amount("0")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);
        final Account account2 = Account.buildAccount(accountRequestDTO2, customer1);

        final AccountTransferRequestDTO accountTransferRequestDTO =
                AccountTransferRequestDTO
                        .builder()
                        .customerNumber(customer1.getCustomerNumber().toString())
                        .fromAccount(account1.getAccountNumber().toString())
                        .toAccount(account2.getAccountNumber().toString())
                        .transferAmount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.empty());

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account2.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account2));

        final ResponseEntity<List<AccountResponseDTO>> response =
                accountController.transferAccount(accountTransferRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void transferAccountFailureInsufficientFunds() {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("49")
                        .build();

        final AccountRequestDTO accountRequestDTO2 =
                AccountRequestDTO
                        .builder()
                        .amount("0")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);
        final Account account2 = Account.buildAccount(accountRequestDTO2, customer1);

        final AccountTransferRequestDTO accountTransferRequestDTO =
                AccountTransferRequestDTO
                        .builder()
                        .customerNumber(customer1.getCustomerNumber().toString())
                        .fromAccount(account1.getAccountNumber().toString())
                        .toAccount(account2.getAccountNumber().toString())
                        .transferAmount("50")
                        .build();

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account1.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account1));

        Mockito.when(accountRepository.findByAccountNumberAndCustomer_CustomerNumber(
                        account2.getAccountNumber(),
                        customer1.getCustomerNumber()))
                .thenReturn(Optional.of(account2));

        final ResponseEntity<List<AccountResponseDTO>> response =
                accountController.transferAccount(accountTransferRequestDTO);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @AfterEach
    void tearDown() {
        accountController = null;
        accountRepository = null;
        customerController = null;
    }
}