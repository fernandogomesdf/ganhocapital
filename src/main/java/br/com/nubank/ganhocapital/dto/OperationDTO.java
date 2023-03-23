package br.com.nubank.ganhocapital.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OperationDTO {
    String operation;
    BigDecimal unitCost;
    Integer quantity;
}
