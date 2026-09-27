package org.blinkapp.repository;

import org.blinkapp.dto.Treatments;
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

import static org.jooq.records.Tables.TREATMENTS;


@Repository
public class TreatmentsRepository {

    private final DSLContext dsl;

    private static final Logger log = LoggerFactory.getLogger(TreatmentsRepository.class);

    @Autowired
    public TreatmentsRepository(DSLContext dsl) { this.dsl = dsl;}

    private final JdbcMapper<Treatments> treatmentsMapper = JdbcMapperFactory.newInstance().newMapper(Treatments.class);

    private ResponseEntity<String> validateTreatmentInput(String treatmentName, String treatmentDescription) {
        if(treatmentName == null || treatmentName.trim().isEmpty() || treatmentDescription == null || treatmentDescription.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Title or description cannot be empty");
        }
        if(treatmentName.length() > 255) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Title cannot exceed 255 characters");
        }
        if(treatmentDescription.length() > 400) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Title cannot exceed 400 characters");
        }
        return null;
    }

    public List<Treatments> getTreatmentsPage() {
        try (ResultSet rs = dsl.select(
                        TREATMENTS.TREATMENTID,
                        TREATMENTS.TREATMENTNAME,
                        TREATMENTS.TREATMENTDESCRIPTION
                )
                .from(TREATMENTS)
                .orderBy(TREATMENTS.TREATMENTID.asc())
                .fetchResultSet()) {
            return treatmentsMapper.stream(rs).toList();
        } catch (SQLException e) {
            log.error("Failed to fetch Treatment History", e);
            return Collections.emptyList();
        }
    }

    public ResponseEntity<String> upsertTreatmentDescription(String treatmentName, String treatmentDescription) {
        ResponseEntity<String> validationError = validateTreatmentInput(treatmentName, treatmentDescription);
        if(validationError != null) {
            return validationError;
        }
        try {
            dsl.insertInto(TREATMENTS)
                    .set(TREATMENTS.TREATMENTNAME, treatmentName)
                    .set(TREATMENTS.TREATMENTDESCRIPTION, treatmentDescription)
                    .execute();

            return ResponseEntity.ok("New Treatment Inserted");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert treatment");
        }
    }

    public ResponseEntity<String> deleteTreatment(int treatmentID) {
        try {
            dsl.deleteFrom(TREATMENTS)
                    .where(TREATMENTS.TREATMENTID.eq(treatmentID))
                    .execute();

            return ResponseEntity.ok("Treatment Deleted");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete treatment");
        }
    }

    public ResponseEntity<String> updateTreatment(Treatments treatment) {
        ResponseEntity<String> validationError = validateTreatmentInput(treatment.getTreatmentName(), treatment.getTreatmentDescription());
        if(validationError != null) {
            return validationError;
        }
        try {
            dsl.update(TREATMENTS)
                    .set(TREATMENTS.TREATMENTNAME, treatment.getTreatmentName())
                    .set(TREATMENTS.TREATMENTDESCRIPTION, treatment.getTreatmentDescription())
                    .where(TREATMENTS.TREATMENTID.eq(treatment.getTreatmentID()))
                    .execute();
            return ResponseEntity.ok("Treatment Updated");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update treatment");
        }
    }
}
