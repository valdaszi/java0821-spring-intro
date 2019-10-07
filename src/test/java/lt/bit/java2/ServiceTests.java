package lt.bit.java2;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ServiceTests {

    EUCentralBankService bankService = new EUCentralBankService();

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
