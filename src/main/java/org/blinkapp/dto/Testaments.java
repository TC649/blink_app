package org.blinkapp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Testaments {
    private int TestamentID;
    private String TreatmentDescription;
    private String CustomerName;
    private String TreatmentName;
    private int TreatmentID;
}
