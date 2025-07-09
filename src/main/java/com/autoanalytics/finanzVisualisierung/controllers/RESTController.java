package com.autoanalytics.finanzVisualisierung.controllers;

import com.autoanalytics.finanzVisualisierung.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class RESTController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DataService dataService;

    public RESTController(DataService dataService) {
        this.dataService = dataService;
    }

    

    @GetMapping("/index")
    public String getIndex(Model model) {
        List<String> companies = dataService.getAllCompanies();
        String firstCompany = dataService.getFirstCompany();
        int latestYearOf = dataService.getLatestYearOf(firstCompany);
        List<Integer> jahre = dataService.getAllYearsByUnternehmen(firstCompany);
        Map<Integer, Map<String, Object[][]>> firstCompanyData = dataService.getDataArray(firstCompany);

        try {
            String firstCompanyDataJson = objectMapper.writeValueAsString(firstCompanyData);

            model.addAttribute("companies", companies);
            model.addAttribute("firstCompany", firstCompany);
            model.addAttribute("latestYearOfSelectedCompany", latestYearOf);
            model.addAttribute("jahre", jahre);
            model.addAttribute("firstCompanyData", firstCompanyDataJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }



    // Add REST endpoint for GET request to get data depends on companyName
    @GetMapping("/data-by-company")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDataOfCompany(@RequestParam String companyName) {
        // processing based on the company
        Map<Integer, Map<String, Object[][]>> companyData = dataService.getDataArray(companyName);
        List<Integer> yearsOfCompany = dataService.getAllYearsByUnternehmen(companyName);

        Map<String, Object> response = new HashMap<>();
        response.put("companyName", companyName);
        response.put("companyData", companyData);
        response.put("yearsOfCompany", yearsOfCompany);
        response.put("message", "this is a message " + companyName);

        return ResponseEntity.ok(response);
    }



}


