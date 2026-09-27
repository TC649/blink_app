package org.blinkapp.repository;

import org.blinkapp.dto.Title;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.jooq.impl.DSL.max;

import static org.jooq.records.Tables.TITLE;

@Repository
public class TitleRepository {

    private final DSLContext dsl;

    private static final Logger log = LoggerFactory.getLogger(TitleRepository.class);

    @Autowired
    public TitleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private final JdbcMapper<Title> titleMapper = JdbcMapperFactory.newInstance().newMapper(Title.class);

    private SelectJoinStep<?> baseTitleSelect() {
        return dsl.select(
                TITLE.TITLEID,
                TITLE.DESCRIPTION,
                TITLE.DATE
        ).from(TITLE);
    }

    public Title getTitlePage() {
        Record record = dsl.select(
                TITLE.TITLEID,
                TITLE.DESCRIPTION,
                TITLE.DATE
        ).from(TITLE).where(TITLE.TITLEID.eq(
                dsl.select(max(TITLE.TITLEID)).from(TITLE)
        )).fetchOne();

        if (record == null) {
            throw new NullPointerException("Title information not found");
        }

        Title title = new Title();
        title.setTitleID(record.get(TITLE.TITLEID));
        title.setDescription(record.get(TITLE.DESCRIPTION));
        title.setDate(record.get(TITLE.DATE));
        return title;
    }

    public ResponseEntity<String> upsertTitleDescription(String description) {
        if(description == null || description.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Description cannot be empty");
        }
        if(description.length() > 600) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Description cannot exceed 600 characters");
        }
        try {
            dsl.insertInto(TITLE)
                    .set(TITLE.DESCRIPTION, description)
                    .set(TITLE.DATE, LocalDate.now())
                    .execute();

            return ResponseEntity.ok("Title upserted successfully");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upsert title: " + e.getMessage());
        }
    }

    public List<Title> getTitleHistory() {
        try (ResultSet rs = baseTitleSelect().fetchResultSet()) {
            return titleMapper.stream(rs).toList();
        } catch (SQLException e) {
            log.error("Failed to fetch any Title History");
            return Collections.emptyList();
        }
    }

}
