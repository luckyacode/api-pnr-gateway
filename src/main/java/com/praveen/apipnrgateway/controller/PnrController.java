package com.praveen.apipnrgateway.controller;

import com.praveen.apipnrgateway.dto.ApiResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.service.PnrService;
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
public class PnrController {
    private final PnrService pnrService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PNR>> getById(@PathVariable String id)  {
        return pnrService.getOptionalPnrById(id).map(pnr ->
                ApiResponse.ok(pnr, "PNR Found")).orElseGet(
                () -> ApiResponse.notFound("PNR with " + id + " not found"));
    }

    @GetMapping("/{id}/edifact-message")
    public ResponseEntity<ApiResponse<String>> getPnrEditfactMessage(@PathVariable String id) {
        return pnrService.getEdifactMessageByPnrId(id).
                map(pnr -> ApiResponse.ok(pnr, "EDIFACT Message Generated Successfully"))
                .orElseGet(() -> ApiResponse.notFound("UN/EDIFACT generation failed: PNR Data with id " + id + " not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PNR>>> getAll()  {
       return ApiResponse.ok(pnrService.findAll(),"All PNR Data Fetched");
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<PNR>>> getAllByStatus(@PathVariable String status)  {
       return ApiResponse.ok(pnrService.findAllByStatus(status),"PNR Data Fetched For Booking Status "+status);
    }

}
