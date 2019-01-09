package com.stevesouza.camel.experiment4.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonName {
    private String personMiddleName;
    private String personGivenName;
    private String personSurName;
}