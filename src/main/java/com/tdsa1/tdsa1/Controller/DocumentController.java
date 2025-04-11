package com.tdsa1.tdsa1.Controller;
import com.tdsa1.tdsa1.Service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload-docx")
    public String uploadDocxFile(@RequestParam("file") MultipartFile file) {
        try {
            documentService.indexDocxFile(file);
            return "DOCX file indexed successfully!";
        } catch (Exception e) {
            return "Error indexing file: " + e.getMessage();
        }
    }

}
