package hello.wsdassignment2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "jwt.secret=tempjwtsecretkeytempjwtsecretkeytempjwtsecretkeytempjwtsecretkeytempjwtsecretkeytempjwtsecretkeytempjwtsecretkeytempjwtsecretkey")
class WsdAssignment2ApplicationTests {

    @Test
    void contextLoads() {
    }

}
