package com.stevesouza.camel.experiment2.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonName {
    private String personMiddleName;
    private String personGivenName;
    private String personSurName;
}