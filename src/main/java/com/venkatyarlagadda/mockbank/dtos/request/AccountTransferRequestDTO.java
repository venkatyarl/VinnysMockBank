package com.venkatyarlagadda.mockbank.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountTransferRequestDTO implements Serializable {

    @NotNull
    private String customerNumber;
    @NotNull
    private String toAccount;
    @NotNull
    private String fromAccount;

    @NotNull
    private String transferAmount;
}
