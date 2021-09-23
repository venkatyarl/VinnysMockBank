package com.venkatyarlagadda.mockbank.dtos.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Builder
@Data
public class CustomerDTO implements Serializable {

    @NotNull
    private UUID customerNumber;

    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
