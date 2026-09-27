package org.blinkapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Treatments {
    private int TreatmentID;
    private String TreatmentName;
    private String TreatmentDescription;
}
