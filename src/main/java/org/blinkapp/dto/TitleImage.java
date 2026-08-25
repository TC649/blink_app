package org.blinkapp.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class TitleImage {
    private int ID;
    private String imageName;
    private Date createdAt;
}
