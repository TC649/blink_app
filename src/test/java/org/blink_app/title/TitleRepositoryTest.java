package org.blink_app.title;

import com.example.config.JooqTestConfig;
import org.blinkapp.Application;
import org.blinkapp.dto.Title;
import org.blinkapp.repository.TitleRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {Application.class, JooqTestConfig.class})
@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TitleRepositoryTest {

    private TitleRepository titleRepository;

    @Autowired
    private DSLContext dslContext;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db").withUsername("username").withPassword("password")
            .withInitScript("db/test-data.sql");

    static {
        postgreSQLContainer.start();
    }

    public static Stream<Arguments> expectedTitles() {
        return Stream.of(
                Arguments.of(1, "Welcome to Lashes", LocalDate.parse("2026-08-23")),
                Arguments.of(2, "Not welcome to Lashes", LocalDate.parse("2026-08-21")),
                Arguments.of(3, "Welcome to blink....", LocalDate.parse("2026-08-20"))
        );
    }

    @BeforeEach
    void setUp() {
        titleRepository = new TitleRepository(dslContext);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("DB_PASSWORD", postgreSQLContainer::getPassword);
    }

    @Test
    void testConnectionToDatabase() { assertNotNull(titleRepository);}

    @Test
    public void getTitleDescription() {
        Title title = titleRepository.getTitlePage();

        assertNotNull(title, "Title Description is null!");

        assertEquals(3, title.getTitleID());
        assertEquals("Welcome to blink....", title.getDescription());
        assertEquals(LocalDate.parse("2026-08-20"), title.getDate());
    }

    @Test
    public void upsertTitleDescription() {
        titleRepository.upsertTitleDescription("You're NOT welcome to lashes!");

        Title title = titleRepository.getTitlePage();

        assertEquals(4, title.getTitleID());
        assertEquals("You're NOT welcome to lashes!", title.getDescription());
        assertEquals(LocalDate.now(), title.getDate());
    }

    @Test
    public void upsertEmptyDescription() {
        ResponseEntity<String> response = titleRepository.upsertTitleDescription("");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Description cannot be empty", response.getBody());
    }

    @Test
    public void upsertTooLongDescription() {
        String tooLong = "a".repeat(601);
        ResponseEntity<String> response = titleRepository.upsertTitleDescription(tooLong);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("expectedTitles")
    public void getTitleHistory(int titleID, String description, LocalDate date) {
        List<Title> history = titleRepository.getTitleHistory();

        Title title = history.stream()
                .filter(t -> t.getTitleID() == titleID)
                .findFirst()
                .orElseThrow(() -> new AssertionError("TitleID " + titleID + " not found"));

        assertEquals(description, title.getDescription());
        assertEquals(date, title.getDate());

    }
}
