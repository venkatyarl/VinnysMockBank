package com.venkatyarlagadda.mockbank.controllers;

import com.venkatyarlagadda.mockbank.dtos.request.AccountRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.response.CustomerResponseDTO;
import com.venkatyarlagadda.mockbank.entity.Account;
import com.venkatyarlagadda.mockbank.entity.Customer;
import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;
import com.venkatyarlagadda.mockbank.repositories.AccountRepository;
import com.venkatyarlagadda.mockbank.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

class CustomerControllerTest {
    private CustomerController customerController;
    private PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private AccountRepository accountRepository = Mockito.mock(AccountRepository.class);

    @BeforeEach
    void setUp() {
        customerController = new CustomerController(passwordEncoder, customerRepository, accountRepository);
    }

    @Test
    public void createCustomerSuccess() {
        final CustomerRequestDTO customerRequestDTO =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("FirstName")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer = Customer.buildCustomer(customerRequestDTO);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("password");
        Mockito.when(customerRepository.save(Mockito.any())).thenReturn(customer);

        final ResponseEntity<CustomerResponseDTO> response = customerController.createCustomer(customerRequestDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(Objects.requireNonNull(response.getBody()).getCustomerNumber());
    }

    @Test
    public void createCustomerAlreadyExists() {
        final CustomerRequestDTO customerRequestDTO =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("FirstName")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer = Customer.buildCustomer(customerRequestDTO);

        Mockito.when(customerRepository.findCustomerByEmail(
                customerRequestDTO.getEmail())).thenReturn(Optional.of(customer));

        final ResponseEntity<CustomerResponseDTO> response = customerController.createCustomer(customerRequestDTO);

        Assertions.assertEquals(HttpStatus.valueOf(409), response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void getCustomers() {
        final CustomerRequestDTO customerRequestDTO1 =
                CustomerRequestDTO
                        .builder()
                        .email("test@test.com")
                        .firstName("First")
                        .lastName("LastName")
                        .password("password")
                        .build();
        final CustomerRequestDTO customerRequestDTO2 =
                CustomerRequestDTO
                        .builder()
                        .email("test2@test.com")
                        .firstName("Second")
                        .lastName("LastName")
                        .password("password")
                        .build();

        final Customer customer1 = Customer.buildCustomer(customerRequestDTO1);
        final Customer customer2 = Customer.buildCustomer(customerRequestDTO2);

        final AccountRequestDTO accountRequestDTO1 =
                AccountRequestDTO
                        .builder()
                        .amount("100")
                        .build();

        final Account account1 = Account.buildAccount(accountRequestDTO1, customer1);
        final Account account2 = Account.buildAccount(accountRequestDTO1, customer1);

        Mockito.when(accountRepository.findByCustomer_CustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(List.of(account1, account2));
        Mockito.when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        final ResponseEntity<List<CustomerResponseDTO>> response = customerController.getCustomers();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertNotNull(response.getBody().get(0).getCustomerNumber());
        Assertions.assertNotNull(response.getBody().get(0).getEmail());
        Assertions.assertNotNull(response.getBody().get(0).getFirstName());
        Assertions.assertNotNull(response.getBody().get(0).getLastName());
        Assertions.assertNotNull(response.getBody().get(0).getListOfAccounts());
        Assertions.assertEquals(2, response.getBody().get(0).getListOfAccounts().size());
        Assertions.assertNotNull(response.getBody().get(0).getListOfAccounts().get(0).getAccountNumber());
        Assertions.assertNotNull(response.getBody().get(0).getListOfAccounts().get(0).getAccountBalance());
        Assertions.assertNotNull(response.getBody().get(0).getListOfAccounts().get(0).getCustomerNumber());
    }

    @Test
    void getCustomerWithCustomerNumberFound() throws InvalidUUIDException {
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
        Mockito.when(customerRepository.findCustomerByCustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));

        final ResponseEntity<CustomerResponseDTO> response =
                customerController.getCustomerWithCustomerNumber(customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getCustomerWithCustomerNumberNotFound() throws InvalidUUIDException {
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
        Mockito.when(customerRepository.findCustomerByCustomerNumber(UUID.randomUUID()))
                .thenReturn(Optional.empty());

        final ResponseEntity<CustomerResponseDTO> response =
                customerController.getCustomerWithCustomerNumber(customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void updateCustomerDetailsFound() throws InvalidUUIDException {
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

        final CustomerDTO customerDTO = CustomerDTO
                .builder()
                .customerNumber(customer1.getCustomerNumber())
                .firstName("changedFirstName")
                .lastName("changedLastName")
                .build();


        Mockito.when(accountRepository.findByCustomer_CustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(List.of(account1));
        Mockito.when(customerRepository.findCustomerByCustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));

        final ResponseEntity<CustomerResponseDTO> response =
                customerController.updateCustomerDetails(customerDTO,
                        customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getFirstName(), customerDTO.getFirstName());
        Assertions.assertEquals(response.getBody().getLastName(), customerDTO.getLastName());
    }

    @Test
    void updateCustomerDetailsNotFound() throws InvalidUUIDException {
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

        final CustomerDTO customerDTO = CustomerDTO
                .builder()
                .customerNumber(UUID.randomUUID())
                .firstName("changedFirstName")
                .lastName("changedLastName")
                .build();


        Mockito.when(accountRepository.findByCustomer_CustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(List.of(account1));

        Mockito.when(customerRepository.findCustomerByCustomerNumber(customerDTO.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));

        final ResponseEntity<CustomerResponseDTO> response =
                customerController.updateCustomerDetails(customerDTO,
                        customer1.getCustomerNumber().toString());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void deleteCustomerWithAccountBalanceEqualsZero() throws InvalidUUIDException {
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
                        .amount("0")
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
        Mockito.when(customerRepository.findCustomerByCustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));
        Mockito.doNothing().when(customerRepository).delete(Mockito.any());

        final boolean response =
                customerController.deleteCustomerWithCustomerNumber(
                        customer1.getCustomerNumber().toString());

        Assertions.assertTrue(response);
    }

    @Test
    void deleteCustomerWithAccountBalanceGreaterThanZero() throws InvalidUUIDException {
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
        Mockito.when(customerRepository.findCustomerByCustomerNumber(customer1.getCustomerNumber()))
                .thenReturn(Optional.of(customer1));
        Mockito.doNothing().when(customerRepository).delete(Mockito.any());

        final boolean response =
                customerController.deleteCustomerWithCustomerNumber(
                        customer1.getCustomerNumber().toString());

        Assertions.assertFalse(response);
    }

    @AfterEach
    void tearDown() {
        customerController = null;
        passwordEncoder = null;
        customerRepository = null;
        accountRepository = null;
    }

}