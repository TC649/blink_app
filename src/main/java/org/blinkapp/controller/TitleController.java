package org.blinkapp.controller;

import org.blinkapp.dto.Title;
import org.blinkapp.repository.TitleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/title")
public class TitleController {

    private final TitleRepository titleRepository;

    public TitleController(TitleRepository titleRepository) {
        this.titleRepository = titleRepository;
    }

    @GetMapping
    public Title getTitle() {
        return titleRepository.getTitlePage();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    @PatchMapping("/update")
    public ResponseEntity<String> upsertTitleDescription(String description) {
        return titleRepository.upsertTitleDescription(description);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    @GetMapping("/history")
    public List<Title> getTitleHistory() {
        return titleRepository.getTitleHistory();
    }

}
