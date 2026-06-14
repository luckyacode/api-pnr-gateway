package com.praveen.apipnrgateway.controller;

import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.service.PNRService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pnr")
@RequiredArgsConstructor
public class PNRController {
    private final PNRService pnrService;

    @GetMapping("/getById/{id}")
    public PNR getById(@PathVariable String id) throws Exception {
       return  pnrService.getPNRById(id);
    }

    @GetMapping("/getPNRMessageById/{id}")
    public String getPNRMessageById(@PathVariable String id) throws Exception {
       return  pnrService.getPNRMessageById(id);
    }

    @GetMapping("/getAll")
    public List<PNR> getAll()  {
       return  pnrService.findAll();
    }



}
