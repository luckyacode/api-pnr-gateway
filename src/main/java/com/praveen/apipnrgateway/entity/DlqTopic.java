package com.praveen.apipnrgateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DlqTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String pnrId;
    private String sourceTopic;
    private String deadLetterTopic;
    private String reason;
    @Lob
    private String event;
    private Instant loggedAt;
    @Builder.Default
    private Boolean resolved = false;
}
