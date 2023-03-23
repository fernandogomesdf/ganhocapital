package br.com.nubank.ganhocapital.service;

import br.com.nubank.ganhocapital.dto.OperationDTO;
import br.com.nubank.ganhocapital.dto.SimulationDTO;
import br.com.nubank.ganhocapital.dto.TaxDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConverterService {

    public List<List<Object>> convertStringToJson(String json){
        String[] simulacoes = json.split("]");
        List<List<Object>> arrSimulations = new ArrayList<>();
        for (String simulation : simulacoes) {
            JsonParser springParser = JsonParserFactory.getJsonParser();
            arrSimulations.add(springParser.parseList(simulation + "]"));
        }
       return arrSimulations;
    }

    public List<List<OperationDTO>> convertObjectListToDTO(List<List<Object>> simulations) {
        List<List<OperationDTO>> simulationsDTO = new ArrayList<>();
        simulations.forEach(simulation -> {
            List<OperationDTO> simulationDTO = new ArrayList<>();
            simulation.forEach(o -> {
                Map<?,?> linha = (Map<?,?>) o;
                OperationDTO operationDTO = new OperationDTO();
                operationDTO.setOperation((String) linha.get("operation"));
                operationDTO.setUnitCost(BigDecimal.valueOf((double) linha.get("unit-cost")));
                operationDTO.setQuantity((Integer) linha.get("quantity"));
                simulationDTO.add(operationDTO);
            });
            simulationsDTO.add(simulationDTO);
        });
        return simulationsDTO;
    }

    public void convertDTOToJson(SimulationDTO simulationDTO) throws JsonProcessingException {
        Map<Integer, BigDecimal> taxesMap = simulationDTO.getTaxes();
        List<TaxDTO> taxesDTO = new ArrayList<>();
        taxesMap.forEach((integer, bigDecimal) -> {
            TaxDTO taxDTO = new TaxDTO();
            taxDTO.setTax(bigDecimal);
            taxesDTO.add(taxDTO);
        });
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(taxesDTO);
        System.out.println(jsonString);
    }
}
