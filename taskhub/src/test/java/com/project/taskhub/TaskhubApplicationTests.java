package com.project.taskhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
            "app.security.jwt.secret=test-secret-that-is-long-enough-for-hmac256",
            "spring.datasource.url=jdbc:h2:mem:taskhub;DB_CLOSE_DELAY=-1",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.flyway.enabled=false"
        })
class TaskhubApplicationTests {

    @Test
    void contextLoads() {}
}
