package com.tdsa1.tdsa1.Service;
import com.tdsa1.tdsa1.Document.DocumentModel;
import com.tdsa1.tdsa1.Document.DocumenMetaData;
import com.tdsa1.tdsa1.Document.DocumentRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class DocumentService {


    @Autowired
    private final DocumentRepository documentRepository;




    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void upload(MultipartFile file, DocumenMetaData document){


        DocumentModel doc = new DocumentModel();

        doc.setTitle(document.getTitle());
        doc.setAuthor(document.getAuthor());
        doc.setContent(document.getContent());

        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());


  String path= saveFile(file);

doc.setFilePath(path);
        saveFile(file);



        documentRepository.save(doc);



    }

    private String saveFile(MultipartFile file) {
        try {
            // Define the path to store files (local file system or you could use cloud storage)
            String uploadDir = "uploads/"; // Change this to a folder where you want to store files
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);

            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(uploadDir));

            // Save the file to disk
            Files.write(path, file.getBytes());

            return path.toString(); // Return the file path
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to store file");
        }
    }




    public Optional<DocumentModel> findById(String id) {


        return documentRepository.findById(id);

    }

}