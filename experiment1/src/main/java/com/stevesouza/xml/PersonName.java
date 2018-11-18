package com.stevesouza.xml;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonName {
    private String PersonMiddleName;

    private String PersonGivenName;

    private String PersonSurName;

}