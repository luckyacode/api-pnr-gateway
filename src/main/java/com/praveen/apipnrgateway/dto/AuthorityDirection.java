package com.praveen.apipnrgateway.dto;

public enum AuthorityDirection{
    OK("Clear"),DNL("Do  Not Board"),CHCK("Manual Check");
    private String str;
    AuthorityDirection(String str) {
        this.str = str;
    }
}