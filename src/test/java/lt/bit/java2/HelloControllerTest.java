package lt.bit.java2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    HelloController helloController;

    @Test
    public void helloTest() {
        assertThat(helloController).isNotNull();
    }

    @Test
    public void helloShouldReturnCount() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/hello", String.class)).contains("Count:");
    }
}
