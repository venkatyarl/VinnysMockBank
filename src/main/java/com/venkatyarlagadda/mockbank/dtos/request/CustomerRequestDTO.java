package com.venkatyarlagadda.mockbank.dtos.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Builder
@Data
public class CustomerRequestDTO implements Serializable {
    @Email
    private String email;

    @Size(min = 6, max = 8)
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
