package org.blinkapp.controller;

import org.blinkapp.dto.Testaments;
import org.blinkapp.repository.TestamentsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testaments")
public class TestamentsController {

    private final TestamentsRepository testamentsRepository;

    public TestamentsController(TestamentsRepository testamentsRepository) {
        this.testamentsRepository = testamentsRepository;
    }

    @GetMapping
    public List<Testaments> getTestaments() {
        return testamentsRepository.getTestamentsPage();
    }

    @PostMapping("/add")
    public ResponseEntity<String> upsertTestaments(@RequestBody String description, @RequestBody String customerName, @RequestBody int treatmentID) {
        return testamentsRepository.upsertTestament(description, customerName, treatmentID);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTestaments(@RequestBody int testamentID) {
        return testamentsRepository.deleteTestament(testamentID);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateTestaments(@RequestBody int testamentID, @RequestBody int treatmentID, @RequestBody String customerName, @RequestBody String description ) {
        return testamentsRepository.updateTestament(testamentID, treatmentID, customerName, description);
    }
}
