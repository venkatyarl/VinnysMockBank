package com.venkatyarlagadda.mockbank.controllers;

import com.venkatyarlagadda.mockbank.dtos.request.CustomerDTO;
import com.venkatyarlagadda.mockbank.dtos.request.CustomerRequestDTO;
import com.venkatyarlagadda.mockbank.dtos.response.AccountResponseDTO;
import com.venkatyarlagadda.mockbank.dtos.response.CustomerResponseDTO;
import com.venkatyarlagadda.mockbank.entity.Customer;
import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;
import com.venkatyarlagadda.mockbank.repositories.AccountRepository;
import com.venkatyarlagadda.mockbank.repositories.CustomerRepository;
import com.venkatyarlagadda.mockbank.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Component
public class CustomerController {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerController(final PasswordEncoder passwordEncoder,
                              final CustomerRepository customerRepository,
                              final AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<List<CustomerResponseDTO>> getCustomers() {
        final List<CustomerResponseDTO> listOfCustomersResponseDTOs = new ArrayList<>();
        customerRepository
                .findAll()
                .forEach(customer ->
                        listOfCustomersResponseDTOs.add(
                                CustomerResponseDTO.buildCustomerResponseDTO(customer,
                                        AccountResponseDTO.buildAccountResponseDTO(
                                                accountRepository.findByCustomer_CustomerNumber(
                                                        customer.getCustomerNumber())))));
        return ResponseEntity.ok(listOfCustomersResponseDTOs);
    }

    public Optional<Customer> getCustomerWithCustomerNumber(final UUID customerNumber) {
        return customerRepository.findCustomerByCustomerNumber(customerNumber);
    }

    public ResponseEntity<CustomerResponseDTO> getCustomerWithCustomerNumber(final String customerNumber)
            throws InvalidUUIDException {
        final UUID customerNumberUUID = StringUtils.convertStringToUUID(customerNumber);
        return getCustomerWithCustomerNumber(customerNumberUUID)
                .map(customer -> {
                    final CustomerResponseDTO responseDTO =
                            CustomerResponseDTO.buildCustomerResponseDTO(customer,
                                    AccountResponseDTO.buildAccountResponseDTO(
                                            accountRepository.findByCustomer_CustomerNumber(
                                                    customer.getCustomerNumber())));
                    return new ResponseEntity<>(responseDTO, HttpStatus.FOUND);
                }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<CustomerResponseDTO> updateCustomerDetails(final CustomerDTO customerDTO,
                                                                     final String customerNumber)
            throws InvalidUUIDException {
        final UUID customerNumberUUID = StringUtils.convertStringToUUID(customerNumber);
        return customerRepository.findCustomerByCustomerNumber(customerNumberUUID)
                .map(customer -> {
                    customer.setFirstName(customerDTO.getFirstName());
                    customer.setLastName(customerDTO.getLastName());
                    final CustomerResponseDTO responseDTO =
                            CustomerResponseDTO.buildCustomerResponseDTO(customer,
                                    AccountResponseDTO.buildAccountResponseDTO(
                                            accountRepository.findByCustomer_CustomerNumber(
                                                    customer.getCustomerNumber())));
                    return new ResponseEntity<>(responseDTO, HttpStatus.FOUND);
                }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public boolean deleteCustomerWithCustomerNumber(final String customerNumber)
            throws InvalidUUIDException {
        final UUID customerNumberUUID = StringUtils.convertStringToUUID(customerNumber);
        return customerRepository.findCustomerByCustomerNumber(customerNumberUUID)
                .map(customer -> {
                    if (accountRepository.findByCustomer_CustomerNumber(customer.getCustomerNumber())
                            .stream()
                            .allMatch(account -> account.getAccountBalance().compareTo(BigDecimal.ZERO) == 0)) {
                        accountRepository.deleteAll(
                                accountRepository.findByCustomer_CustomerNumber(
                                        customer.getCustomerNumber()));
                        customerRepository.delete(customer);
                        return true;
                    } else {
                        return false;
                    }
                }).orElse(false);
    }

    public ResponseEntity<CustomerResponseDTO> createCustomer(final CustomerRequestDTO customerRequestDTO) {
        return findCustomerByEmail(customerRequestDTO.getEmail())
                .map(customer -> new ResponseEntity<CustomerResponseDTO>(HttpStatus.valueOf(409))).orElseGet(() -> {
                    customerRequestDTO.setPassword(passwordEncoder.encode(customerRequestDTO.getPassword()));
                    final Customer customer = customerRepository.save(Customer.buildCustomer(customerRequestDTO));
                    final CustomerResponseDTO responseDTO =
                            CustomerResponseDTO.buildCustomerResponseDTO(customer,
                                    AccountResponseDTO.buildAccountResponseDTO(
                                            accountRepository.findByCustomer_CustomerNumber(
                                                    customer.getCustomerNumber())));
                    return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
                });
    }

    public Optional<Customer> findCustomerByEmail(final String email) {
        return customerRepository.findCustomerByEmail(email);
    }
}
