package com.tdsa1.tdsa1.Controller;
import com.tdsa1.tdsa1.Document.DocumenMetaData;
import com.tdsa1.tdsa1.Service.DocumentService;
import com.tdsa1.tdsa1.Document.DocumentModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/document")
public class DocumentController {


    @Autowired
    private final DocumentService documentService;


    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @PostMapping("/upload-metadata")
    public ResponseEntity<String> uploadMetadata(

            @RequestPart("file") MultipartFile file,

            @RequestPart("metadata") DocumenMetaData document
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty");
        }



        documentService.upload(file,document); // Save to Elasticsearch
        return ResponseEntity.ok("File path stored!");
    }



//    @GetMapping("/open-file/{id}")
//    public ResponseEntity<String> openFile(@PathVariable String id) throws IOException {
//        Optional<DocumentModel> doc = documentService.findById(id);
//        if (doc == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Path filePath = Paths.get(doc.getClass());
//        if (!Files.exists(filePath)) {
//            return ResponseEntity.status(404).body("File not found at: " + filePath);
//        }
//
//        // Open file with default system application
//        Desktop.getDesktop().open(filePath.toFile());
//        return ResponseEntity.ok("Opened: " + filePath);
//    }





}