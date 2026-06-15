package com.praveen.apipnrgateway.controller;

import com.praveen.apipnrgateway.dto.ApiResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.service.PNRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pnr-data")
@RequiredArgsConstructor
public class PNRController {
    private final PNRService pnrService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PNR>> getById(@PathVariable String id)  {
        return pnrService.getOptionalPNRById(id).map(pnr ->
                ApiResponse.ok(pnr, "PNR Found")).orElseGet(
                () -> ApiResponse.notFound("PNR with " + id + " not found"));
    }

    @GetMapping("/{id}/edifact-message")
    public ResponseEntity<ApiResponse<String>> getPNRMessageById(@PathVariable String id) {
        return pnrService.getEdifactMessageByPnrId(id).
                map(pnr -> ApiResponse.ok(pnr, "EDIFACT Message Generated Successfully"))
                .orElseGet(() -> ApiResponse.notFound("UN/EDIFACT generation failed: PNR Data with id " + id + " not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PNR>>> getAll()  {
       return ApiResponse.ok(pnrService.findAll(),"All PNR Data Fetched");
    }

}
