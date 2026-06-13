package com.praveen.apipnrgateway.dto;


import lombok.Getter;

@Getter
public enum AuthorityDirection {
    OK("OK to Board"),       // ✈️ Fully cleared for international travel
    DNL("Do Not Board"),     // ❌ Flags security/immigration restrictions (Do Not Load)
    CHCK("Manual Check");    // ⚠️ Requires human visa/document inspection at desk

    // Standard Getter to fetch the clean string value
    private final String description;

    // Enum Constructor
    AuthorityDirection(String description) {
        this.description = description;
    }

}