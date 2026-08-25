package org.blinkapp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Title {
    private int TitleID;
    private String Description;
    private LocalDate Date;
}
