package com.praveen.apipnrgateway;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Builder
public class Passenger {
    private int id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String country;

    // Constructor parsing your specific data format
    public Passenger(String[] csvRow) {
        this.id = Integer.parseInt(csvRow[0].trim());
        this.fullName = csvRow[1].trim();
        this.firstName = csvRow[2].trim();
        this.lastName = csvRow[3].trim();
        this.email = csvRow[4].trim();
        this.phone = csvRow[5].trim();
        this.city = csvRow[6].trim();
        this.country = csvRow[7].trim();
    }

}