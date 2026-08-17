package org.blinkapp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class Title {
    private final int ID;
    private final String Description;
    private final int ImageID;
    private final String ImageName;
    private final Date Date;
}
