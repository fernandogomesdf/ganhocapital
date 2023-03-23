package br.com.nubank.ganhocapital.service;

import br.com.nubank.ganhocapital.dto.OperationDTO;
import br.com.nubank.ganhocapital.dto.SimulationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CalculatorService {

    final
    ConverterService converterService;

    public CalculatorService(ConverterService converterService) {
        this.converterService = converterService;
    }

    public void calculateTax(String json) throws JsonProcessingException {
        List<List<Object>> simulations = converterService.convertStringToJson(json);
        List<List<OperationDTO>> simulationsDTO = converterService.convertObjectListToDTO(simulations);
        for (List<OperationDTO> operationDTOS : simulationsDTO) {
            calculateSimulation(operationDTOS);
        }
    }

    private void calculateSimulation(List<OperationDTO> operationDTOS) throws JsonProcessingException {
        SimulationDTO simulationDTO = new SimulationDTO();
        simulationDTO.setOperations(operationDTOS);
        calculateOperations(operationDTOS, simulationDTO);
        converterService.convertDTOToJson(simulationDTO);
    }

    private void calculateOperations(List<OperationDTO> operationDTOS, SimulationDTO simulationDTO) {
        AtomicInteger index = new AtomicInteger();
        operationDTOS.forEach(operationDTO -> {
            if ("buy".equals(operationDTO.getOperation())) {
                simulationDTO.getBuyOperations().add(operationDTO);
                BigDecimal tax = new BigDecimal("0.00");
                simulationDTO.getTaxes().put(index.get(), tax);
            }
            if ("sell".equals(operationDTO.getOperation())) {
                simulationDTO.getSellOperations().add(operationDTO);
                calculateSellTax(simulationDTO, index, operationDTO);
                removeIfAllSold(simulationDTO, operationDTO);
            }
            index.getAndIncrement();
        });
    }

    private void calculateSellTax(SimulationDTO simulationDTO, AtomicInteger index, OperationDTO operationDTO) {
        double avgBuyCost = calculateBuyingWeightedAverage(simulationDTO.getBuyOperations()).doubleValue();
        double sellCost = operationDTO.getUnitCost().doubleValue();
        double sellPrice = sellCost * ((double) operationDTO.getQuantity());

        double buyAvgPrice = avgBuyCost * operationDTO.getQuantity();
        double profit = sellPrice - buyAvgPrice;
        double taxD = sellPrice >= 20000 ? (profit + simulationDTO.getLoss()) * 0.20 : 0.00;
        taxD = taxD <= 0 ? 0 : taxD;

        if (simulationDTO.getLoss() < 0 || profit < 0) {
            simulationDTO.setLoss(simulationDTO.getLoss() + profit);
        }
        BigDecimal tax = new BigDecimal(taxD);
        tax = tax.setScale(2, RoundingMode.HALF_UP);
        simulationDTO.getTaxes().put(index.get(), tax);
    }

    private void removeIfAllSold(SimulationDTO simulationDTO, OperationDTO operationDTO) {
        simulationDTO.setSoldSharesQuantity(simulationDTO.getSoldSharesQuantity() + operationDTO.getQuantity());
        if (simulationDTO.getSoldSharesQuantity()
                >= ((LinkedList<OperationDTO>) simulationDTO.getBuyOperations()).getFirst().getQuantity()) {
            ((LinkedList<OperationDTO>) simulationDTO.getBuyOperations()).removeFirst();
            simulationDTO.setSoldSharesQuantity(0);
        }
    }

    private BigDecimal calculateBuyingWeightedAverage(List<OperationDTO> buyOperations) {
        BigDecimal weightedAverage;
        if (buyOperations.size() == 1) {
            weightedAverage = ((LinkedList<OperationDTO>)buyOperations).getFirst().getUnitCost();
        } else {
            double numerator = 0.0;
            double divisor = 0.0;
            for (OperationDTO buyOp : buyOperations) {
                double multiply = buyOp.getQuantity() * buyOp.getUnitCost().doubleValue();
                numerator += multiply;
                divisor += (double) buyOp.getQuantity();
            }
            weightedAverage = BigDecimal.valueOf(numerator / divisor);
        }

        return weightedAverage;
    }
}
