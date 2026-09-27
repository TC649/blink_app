package org.blinkapp.repository;

import org.blinkapp.dto.Testaments;
import org.jooq.DSLContext;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.jooq.records.Tables.TESTAMENTS;
import static org.jooq.records.tables.Treatments.TREATMENTS;

@Repository
public class TestamentsRepository {

    private final DSLContext dsl;

    private static final Logger log = LoggerFactory.getLogger(TestamentsRepository.class);

    @Autowired
    public TestamentsRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private final JdbcMapper<Testaments> testamentsJdbcMapper = JdbcMapperFactory.newInstance().newMapper(Testaments.class);

    private ResponseEntity<String> validateTestamentInput(String description, String customerName) {
        if(customerName == null || customerName.isEmpty() || description == null || description.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Customer name or description cannot be empty");
        }
        if(description.length() > 500) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Description is longer than 500 characters");
        }
        if(customerName.length() > 100) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Customer name is longer than 100 characters");
        }
        return null;
    }

    public List<Testaments> getTestamentsPage() {
        try (ResultSet rs = dsl.select(
                        TESTAMENTS.TESTAMENTID,
                        TESTAMENTS.TREATMENTDESCRIPTION,
                        TESTAMENTS.CUSTOMERNAME,
                        TREATMENTS.TREATMENTNAME)
                .from(TESTAMENTS.join(TREATMENTS).on(TESTAMENTS.TREATMENTID.eq(TREATMENTS.TREATMENTID)))
                .orderBy(TESTAMENTS.TESTAMENTID.asc())
                .fetchResultSet()) {
            return testamentsJdbcMapper.stream(rs).toList();
        } catch (SQLException e) {
            log.error("Failed to fetch Testaments", e);
            return Collections.emptyList();
        }
    }

    public ResponseEntity<String> upsertTestament(String description, String customerName, int treatmentID) {
        ResponseEntity<String> validationError = validateTestamentInput(description, customerName);
        if(validationError != null) {
            return validationError;
        }
        try {
            dsl.insertInto(TESTAMENTS)
                    .set(TESTAMENTS.TREATMENTID, treatmentID)
                    .set(TESTAMENTS.CUSTOMERNAME, customerName)
                    .set(TESTAMENTS.TREATMENTDESCRIPTION, description)
                    .execute();

            return ResponseEntity.ok("Testament Inserted");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert testament");
        }
    }

    public ResponseEntity<String> deleteTestament(int testamentID) {
        try {
            dsl.deleteFrom(TESTAMENTS)
                    .where(TESTAMENTS.TESTAMENTID.eq(testamentID))
                    .execute();

            return ResponseEntity.ok("Testament Deleted");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert testament");
        }
    }

    public ResponseEntity<String> updateTestament(int TestamentID, int TreatmentID, String CustomerName, String Description) {
        ResponseEntity<String> validationError = validateTestamentInput(Description, CustomerName);

        if (validationError != null) {
            return validationError;
        }

        try {
            dsl.update(TESTAMENTS)
                    .set(TESTAMENTS.TREATMENTDESCRIPTION, Description)
                    .set(TESTAMENTS.CUSTOMERNAME, CustomerName)
                    .set(TESTAMENTS.TREATMENTID, TreatmentID)
                    .where(TESTAMENTS.TESTAMENTID.eq(TestamentID))
                    .execute();

            return ResponseEntity.ok("Testament Updated");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update testament");
        }
    }





}
