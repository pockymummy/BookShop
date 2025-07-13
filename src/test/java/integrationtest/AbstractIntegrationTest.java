package integrationtest;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
public abstract class AbstractIntegrationTest {
    private static final String POSTGRES_IMAGE = "postgres:latest";
    private static final String MONGO_IMAGE = "mongo:4.4.6";
    private static final String DATABASE_NAME = "bookstore";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "yourpassword";

    @Container
    static final PostgreSQLContainer<?> postgresqlContainer = initPostgresqlContainer();

    @Container
    static final MongoDBContainer mongoDbContainer = initMongoDBContainer();

    private static PostgreSQLContainer<?> initPostgresqlContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(DATABASE_USER)
                .withPassword(DATABASE_PASSWORD);
        container.start();
        return container;
    }

    private static MongoDBContainer initMongoDBContainer() {
        MongoDBContainer container = new MongoDBContainer(MONGO_IMAGE);
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.data.mongodb.uri", mongoDbContainer::getReplicaSetUrl);
    }
}
