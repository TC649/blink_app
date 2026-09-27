package org.blink_app.treatments;

import com.example.config.JooqTestConfig;
import org.blinkapp.Application;
import org.blinkapp.dto.Treatments;
import org.blinkapp.repository.TreatmentsRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class TreatmentRepositoryTest {

    private TreatmentsRepository treatmentsRepository;

    @Autowired
    private DSLContext dslContext;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db").withUsername("username").withPassword("password")
            .withInitScript("db/test-data.sql");

    static {
        postgreSQLContainer.start();
    }

    public static Stream<Arguments> expectedTreatments() {
        return Stream.of(
                Arguments.of(1, "Deluxe Lash Lift", "Big ol lashes"),
                Arguments.of(2, "Korean Lashes", "very korean"),
                Arguments.of(3, "Chinese Lashes", "very chinese")
        );
    }

    @BeforeEach
    void setUp() {
        treatmentsRepository = new TreatmentsRepository(dslContext);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("DB_PASSWORD", postgreSQLContainer::getPassword);
    }

    @Test
    void testConnectionToDatabase() { assertNotNull(treatmentsRepository);}

    @ParameterizedTest
    @MethodSource("expectedTreatments")
    public void testGetTreatment(int treatmentID, String treatmentName, String treatmentDescription) {
        List<Treatments> history = treatmentsRepository.getTreatmentsPage();

        Treatments treatments = history.stream()
                .filter(t -> t.getTreatmentID() == treatmentID)
                .findFirst()
                .orElseThrow(() -> new AssertionError("TreatmentID " + treatmentID + " not found"));

        assertEquals(treatmentName, treatments.getTreatmentName());
        assertEquals(treatmentDescription, treatments.getTreatmentDescription());
    }

    @Test
    public void testUpdateTreatment() {
        Treatments newTreatment = new Treatments(2, "Irish Lashes", "very irish");
        treatmentsRepository.updateTreatment(newTreatment);

        List<Treatments> treatmentsPage = treatmentsRepository.getTreatmentsPage();

        Treatments updated = treatmentsPage.stream()
                .filter(t -> t.getTreatmentID() == 2)
                .findFirst()
                .orElseThrow(() -> new AssertionError("TreatmentID " + 2 + " not found"));

        assertEquals(2, updated.getTreatmentID());
        assertEquals("Irish Lashes", updated.getTreatmentName());
        assertEquals("very irish", updated.getTreatmentDescription());

    }

    @Test
    public void testUpsertTreatment() {
        treatmentsRepository.upsertTreatmentDescription("Asian lashes", "Lashes that are asian");

        List<Treatments> treatments = treatmentsRepository.getTreatmentsPage();

        assertEquals(4, treatments.getLast().getTreatmentID());
        assertEquals("Asian lashes", treatments.getLast().getTreatmentName());
        assertEquals("Lashes that are asian", treatments.getLast().getTreatmentDescription());
    }

    @Test
    public void testDeleteTreatment() {
        treatmentsRepository.deleteTreatment(1);
        List<Treatments> history = treatmentsRepository.getTreatmentsPage();

        assertEquals(2, history.size());

        assertEquals(2, history.getFirst().getTreatmentID());
        assertEquals("Korean Lashes", history.getFirst().getTreatmentName());
        assertEquals("very korean", history.getFirst().getTreatmentDescription());

        assertEquals(3, history.get(1).getTreatmentID());
        assertEquals("Chinese Lashes", history.get(1).getTreatmentName());
        assertEquals("very chinese", history.get(1).getTreatmentDescription());
    }

}
