package br.com.nubank.ganhocapital;

import br.com.nubank.ganhocapital.service.CalculatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.util.Scanner;

@SpringBootApplication
public class GanhocapitalApplication implements CommandLineRunner {

	final
	CalculatorService calculatorService;

	public GanhocapitalApplication(CalculatorService calculatorService) {
		this.calculatorService = calculatorService;
	}

	public static void main(String[] args) {
		SpringApplication.run(GanhocapitalApplication.class, args);
	}

	@Override
	public void run(String... args) throws JsonProcessingException {
		String input;
		do {
			System.out.print("> ");
			Scanner scanner = new Scanner(System.in);
			input = readKeyboard(scanner);
		} while (exitOrCalculate(input));
	}

	private boolean exitOrCalculate(String input) throws JsonProcessingException  {
		if ("quit".equalsIgnoreCase(input)) {
			System.exit(0);
		} else if (StringUtils.hasText(input)) {
			calculatorService.calculateTax(input);
		}
		return true;
	}

	private static String readKeyboard(Scanner scanner) {
		StringBuilder input;
		String line;
		input = new StringBuilder();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.isEmpty()) {
				break;
			} else {
				input.append(line);
			}
		}
		return input.toString().trim();
	}
}
