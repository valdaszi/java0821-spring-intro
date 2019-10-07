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

	@Test
	public void contextLoads() {
	}

	@Autowired
	EUCentralBankService bankService;

	private InputStream getECB() {
		InputStream is = SpringIntroApplicationTests.class.getClassLoader()
				.getResourceAsStream("ECB.xml");
		assertThat(is).isNotNull();
		return is;
	}

	@Test
	public void testECBResponse() throws IOException {
		assertThat(bankService.exchangeRate("USD", getECB()))
				.isEqualTo(new BigDecimal("1.0979"));

		assertThat(bankService.exchangeRate("ZAR", getECB()))
				.isEqualTo(new BigDecimal("16.6446"));

		assertThat(bankService.exchangeRate("DKK", getECB()))
				.isEqualTo(new BigDecimal("7.4666"));

		assertThat(bankService.exchangeRate("XXX", getECB()))
				.isNull();
	}
}
