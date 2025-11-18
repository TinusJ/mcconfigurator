package com.tinusj.mcconfigurator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {ConfigFileService.class, JsonConfigParser.class, TomlConfigParser.class, CfgConfigParser.class})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class McConfiguratorApplicationTests {

    @Test
    void contextLoads() {
    }

}
