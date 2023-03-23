package br.com.nubank.ganhocapital.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimulationDTO {
    Map<Integer, BigDecimal> taxes;

    List<OperationDTO> operations;
    List<OperationDTO> buyOperations;
    List<OperationDTO> sellOperations;

    Integer soldSharesQuantity = 0;

    double loss = 0.00;

    public Integer getSoldSharesQuantity() {
        return soldSharesQuantity;
    }

    public double getLoss() {
        return loss;
    }

    public Map<Integer, BigDecimal> getTaxes() {
        if (taxes == null) {
            this.taxes = new LinkedHashMap<>();
        }
        return taxes;
    }

    public List<OperationDTO> getBuyOperations() {
        if (buyOperations == null) {
            this.buyOperations = new LinkedList<>();
        }
        return buyOperations;
    }

    public List<OperationDTO> getSellOperations() {
        if (sellOperations == null) {
            this.sellOperations = new LinkedList<>();
        }
        return sellOperations;
    }
}
