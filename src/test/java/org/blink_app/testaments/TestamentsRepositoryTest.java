package org.blink_app.testaments;

import com.example.config.JooqTestConfig;
import org.blinkapp.Application;
import org.blinkapp.dto.Testaments;
import org.blinkapp.repository.TestamentsRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {Application.class, JooqTestConfig.class})
@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TestamentsRepositoryTest {

    private TestamentsRepository testamentsRepository;

    @Autowired
    private DSLContext dslContext;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db").withUsername("username").withPassword("password")
            .withInitScript("db/test-data.sql");

    static {
        postgreSQLContainer.start();
    }

    public static Stream<Arguments> expectedTestaments() {
        return Stream.of(
                Arguments.of(1, "Awful lashes", "Tom Campbell", 1),
                Arguments.of(2, "Amazing lashes", "Sophie Steele", 2),
                Arguments.of(3, "Just fantastic lashes", "Ellen Cooke", 3)

        );
    }

    @BeforeEach
    void setUp() {
        testamentsRepository = new TestamentsRepository(dslContext);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("DB_PASSWORD", postgreSQLContainer::getPassword);
    }

    @Test
    void testConnectionToDatabase() { assertNotNull(testamentsRepository);}

    @ParameterizedTest
    @Order(1)
    @MethodSource("expectedTestaments")
    public void testGetTestament(int testamentID, String description, String customerName) {
        List<Testaments> history =  testamentsRepository.getTestamentsPage();

        Testaments testaments = history.stream()
                .filter(t -> t.getTestamentID() == testamentID)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Testament ID " + testamentID + " not found"));

        assertEquals(description, testaments.getTreatmentDescription());
        assertEquals(customerName, testaments.getCustomerName());
    }

    @Test
    @Order(2)
    public void testUpsertTestament() {
        testamentsRepository.upsertTestament("Beezer lash", "Ellie Hanna", 3);

        List<Testaments> testaments = testamentsRepository.getTestamentsPage();

        Testaments newTestament = testaments.getLast();

        assertEquals(4, newTestament.getTestamentID());
        assertEquals("Beezer lash", newTestament.getTreatmentDescription());
        assertEquals("Ellie Hanna", newTestament.getCustomerName());
    }

    @Test
    @Order(3)
    public void testDeleteTestament() {
        ResponseEntity<String> response = testamentsRepository.deleteTestament(4);
        List<Testaments> testaments = testamentsRepository.getTestamentsPage();

        assertEquals(response.getBody(), "Testament Deleted");
        assertEquals(3, testaments.size());
    }

    @Test
    @Order(4)
    public void testUpdateTestament() {
        ResponseEntity<String> updatedTestaments = testamentsRepository.updateTestament(1, 2, "Tom Campbell", "Amazing lashes");

        List<Testaments> testaments = testamentsRepository.getTestamentsPage();

        assertEquals(updatedTestaments.getBody(), "Testament Updated");

        Testaments updatedTestament = testaments.getFirst();
        assertEquals(1, updatedTestament.getTestamentID());
        assertEquals("Tom Campbell", updatedTestament.getCustomerName());
        assertEquals("Amazing lashes", updatedTestament.getTreatmentDescription());

    }
}
