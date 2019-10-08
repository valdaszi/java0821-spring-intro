package lt.bit.java2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringIntroApplicationTests {

	@Autowired
	CurrencyService currencyService;

	@MockBean
	BankExchageService bankExchageService;

	@Test
	public void testCurrencyService() {
		given(bankExchageService.exchangeRate("USD")).willReturn(new BigDecimal("1.6"));
		given(bankExchageService.exchangeRate("PLN")).willReturn(new BigDecimal("4.0"));

		BigDecimal value = currencyService.exchange("USD", new BigDecimal("128.20"));
		assertThat(value).isEqualTo(new BigDecimal("80.13"));

		value = currencyService.exchange("PLN", new BigDecimal("320.54"));
		assertThat(value).isEqualTo(new BigDecimal("80.14"));
	}
}
