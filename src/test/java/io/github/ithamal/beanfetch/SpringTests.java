package io.github.ithamal.beanfetch;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:28
 */
@ComponentScan("io.github.ithamal.beanfetch")
public class SpringTests {

    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTests.class);
        SimpleTests tests = applicationContext.getBean(SimpleTests.class);
        tests.test();
    }
}
