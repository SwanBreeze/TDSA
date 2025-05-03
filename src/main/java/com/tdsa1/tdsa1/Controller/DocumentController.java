package com.tdsa1.tdsa1.Controller;

import com.tdsa1.tdsa1.Document.*;
import com.tdsa1.tdsa1.Service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/document")
public class DocumentController {


    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String check() {
        return "yes girl it's working";
    }


    @PostMapping(path = "/upload-metadata", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMetadata(

            @RequestPart("file") MultipartFile file,

            @RequestPart("metadata") DocumenMetaData document
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty");
        }

        if (document == null || document.getAuthor() == null || document.getTitle() == null || document.getDateCreation() == null || document.getContent() == null) {
            return ResponseEntity.badRequest().body("Invalid document metadata");
        }


        try {
            documentService.upload(file, document);
            return ResponseEntity.ok("File uploaded and metadata stored!");
        } catch (MaxUploadSizeExceededException ex) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File size exceeds the allowed limit.");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File handling error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading the file and metadata: " + ex.getMessage());
        }

    }

  /*  @GetMapping("/search")
    public List<DocumenMetaData> searchDocument(String word) {
        SearchHits<DocumentModel> hits = documentService.searchDocument(word);
        return hits.get()
                .map(SearchHit::getContent)
                .map(document -> new DocumenMetaData(
                        document.getId(),
                        document.getTitle(),
                        document.getContent(),
                        document.getAuthor(),
                        document.getDateCreation()
                ))
                .collect(Collectors.toList());
    }*/


    @GetMapping("/search/Pv")
    public List<DocumenMetaData> searchPv(@RequestParam String word) {
        return mapHits(documentService.searchDocument(word, "Pv", PvDocument.class, true));
    }

    @GetMapping("/search/Annonce")
    public List<DocumenMetaData> searchAnnonce(@RequestParam String word) {
        return mapHits(documentService.searchDocument(word, "Annonce", AnnanceDocument.class, false));
    }

    @GetMapping("/search/Planning")
    public List<DocumenMetaData> searchPlaning(@RequestParam String word) {
        return mapHits(documentService.searchDocument(word, "Planning", PlanningDocument.class, false));

    }

    @GetMapping("/search/Emploi")
    public List<DocumenMetaData> searchEmploi(@RequestParam String word) {
        return mapHits(documentService.searchDocument(word, "Emploi", EmploiDocument.class, false));

    }


    private List<DocumenMetaData> mapHits(SearchHits<? extends BaseDocument> hits) {
        return hits.get()
                .map(SearchHit::getContent)
                .map(doc -> new DocumenMetaData(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getContent(),
                        doc.getAuthor(),
                        doc.getDateCreation()
                ))
                .collect(Collectors.toList());
    }


    @PostMapping(path = "/upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadIndexes(@RequestParam String indexName, @RequestParam List<MultipartFile> files) {


        try {
            documentService.indexFile(indexName, files);
            return ResponseEntity.ok("File uploaded");

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("Error uploading the file");
        }


    }


}