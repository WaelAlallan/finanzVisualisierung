package com.autoanalytics.finanzVisualisierung.controllers;

import com.autoanalytics.finanzVisualisierung.service.FileStorageService;
import com.autoanalytics.finanzVisualisierung.storage.StorageFileNotFoundException;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final FileStorageService storageService;


    @Autowired
    public FileUploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }



    // handle multiple files
    @PostMapping("/upload-csv")
    public String handleFileUpload(@RequestParam("csvFile") MultipartFile[] files, RedirectAttributes redirectAttributes) {

        try {
            List<String> fileNames = new ArrayList<>();
            Arrays.asList(files).stream().forEach(file -> {
                storageService.store(file);
                fileNames.add(file.getOriginalFilename());
            });

            redirectAttributes.addFlashAttribute("message","You successfully uploaded: " + fileNames + "!");
            //System.out.println(fileNames.toString());
        } catch (Exception e) {
            // TODO
            redirectAttributes.addFlashAttribute("message","Fail to upload files!");
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Fail to upload files!");
        }
        return "redirect:/index";
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}




