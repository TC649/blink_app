package org.blinkapp.controller;

import org.blinkapp.dto.Treatments;
import org.blinkapp.repository.TreatmentsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentsController {

    private final TreatmentsRepository treatmentsRepository;

    public TreatmentsController(TreatmentsRepository treatmentsRepository) {
        this.treatmentsRepository = treatmentsRepository;
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    @GetMapping
    public List<Treatments> getTreatments() {
        return treatmentsRepository.getTreatmentsPage();
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addTreatment(@RequestBody String treatmentName, String description) {
        return treatmentsRepository.upsertTreatmentDescription(treatmentName, description);
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTreatment(@RequestBody int treatmentId) {
        return treatmentsRepository.deleteTreatment(treatmentId);
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity<String> updateTreatment(@RequestBody Treatments treatment) {
        return treatmentsRepository.updateTreatment(treatment);
    }
}
