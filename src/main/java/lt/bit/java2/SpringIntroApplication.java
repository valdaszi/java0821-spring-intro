package lt.bit.java2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.Currency;

@SpringBootApplication
public class SpringIntroApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringIntroApplication.class, args);
	}

	@Bean
	public CounterService counterService() {
		return new CounterService(100);
	}
}

//@Configuration
//class BankConfig {
//
//	@Bean
//	public BankService bankService() {
//		return new BankService();
//	}
//
//	@Bean
//	public B100 b100(BankService bankService) {
//		return new B100(bankService);
//	}
//
//}

interface BankExchageService {
	BigDecimal exchangeRate(String currency);
}

@Service
class EUCentralBankService implements BankExchageService {

	public BigDecimal exchangeRate(String currency) {
		try {
			URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
			InputStream is = url.openStream();

			return exchangeRate(currency, is);

		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}

	public BigDecimal exchangeRate(String currency, InputStream is) {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(is);

			Element root = doc.getDocumentElement();
			Element cube = find(root, "Cube");
			Element cubeCube = find(cube, "Cube");

			return findRate(cubeCube, currency);

		} catch (ParserConfigurationException | IOException | SAXException | NullPointerException e) {
			e.printStackTrace();
		}

		return null;
	}

	private BigDecimal findRate(Element element, String currencyCode) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("Cube") &&
					currencyCode.equals(((Element) node).getAttribute("currency"))) {
				String rate = ((Element) node).getAttribute("rate");
				return new BigDecimal(rate);
			}
		}
		return null;
	}


	private Element find(Element element, String childName) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(childName)) {
				return (Element) nodeList.item(i);
			}
		}
		return null;
	}
}


@Service
class CurrencyService {

	BankExchageService bankService;

	public CurrencyService(BankExchageService bankService) {
		this.bankService = bankService;
	}

	public BigDecimal exchange(String currency, BigDecimal amount) {
		if (amount == null || currency == null || currency.equals("EUR")) {
			return amount;
		} else {
			return amount.divide(
					bankService.exchangeRate(currency),
					2,
					RoundingMode.HALF_UP);
		}
	}
}

class CounterService {

	private int counter = 0;

	public CounterService(int counter) {
		this.counter = counter;
	}

	int count() {
		return ++counter;
	}
}

@Controller
@RequestMapping("/hello")
// GET /hello
// -- is kur gauti CounterService tipo objekta?
// ---- CounterService counterService = ...
// -- HelloController h = new HelloController();
// -- h.counterService = counterService
// ModelMap modelMap = new ModelMap();
// String template = h.index(modelMap);
// templateEngine(template, modelMap);
class HelloController {

	@Autowired
	private CounterService counterService;

	@GetMapping
	public String index(ModelMap model) {
		model.addAttribute("value", counterService.count());
		return "hello";
	}
}

@Controller
@RequestMapping("/employee")
class EmployeeController {

	private final CounterService counterService;

	public EmployeeController(CounterService counterService) {
		this.counterService = counterService;
	}

	@GetMapping
	public String getEmployee(ModelMap model) {
		model.addAttribute("name", "Jonas");
		model.addAttribute("age", 99);
		Account account = new Account();
		account.setName("Petras");
		account.setDate(LocalDate.now());
		model.addAttribute("account", account);

		model.addAttribute("value", counterService.count());

		return "employee"; //  -> ../templates/employee.html
	}

}

class Account {
	private String name;
	private LocalDate date;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}

@Controller
@RequestMapping("/currency")
class CurrencyExchangeRate {

	private final CurrencyService currencyService;

	public CurrencyExchangeRate(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@GetMapping("/{currency}")
	String getCurrencyRate(@PathVariable String currency, ModelMap modelMap) {
		BigDecimal amount = BigDecimal.valueOf(100);
		modelMap.addAttribute("amount", amount);
		modelMap.addAttribute("currency", currency);
		modelMap.addAttribute("value", currencyService.exchange(currency, amount));
		return "currency";
	}

}