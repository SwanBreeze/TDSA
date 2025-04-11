package com.tdsa1.tdsa1.Service;
import com.tdsa1.tdsa1.Model.DocumentModel;

import com.tdsa1.tdsa1.Repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.tdsa1.tdsa1.Repository.DocumentRepository;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    @Autowired

    private DocumentRepository documentRepository ;
    public void AddDocument (DocumentModel document ) {
     documentRepository.save(document);

    }
    public Iterable<DocumentModel> getAllDocumets(){

        return documentRepository.findAll();
    }

    // New method to index DOCX file
    public void indexDocxFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String content = extractor.getText();
            DocumentModel docModel = parseDocxContent(content);
            documentRepository.save(docModel);
        }
    }

    private DocumentModel parseDocxContent(String content) {
        DocumentModel document = new DocumentModel();

        // Extract student name
        String studentName = extractField(content, "L'étudiant \\(e\\) ; ([^\\n]+)");
        document.setStudentName(studentName != null ? studentName.trim() : null);

        // Extract birth date
        String birthDate = extractField(content, "Né\\(e\\) le : ([^\\n]+)");
        document.setBirthDate(birthDate != null ? birthDate.trim() : null);

        // Extract study level (M2 or L3)
        String studyLevel = extractStudyLevel(content);
        document.setStudyLevel(studyLevel);

        // Store the full content
        document.setContent(content);

        return document;
    }

    private String extractField(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractStudyLevel(String content) {
        // Check for M2 (2-ème années Ingénieur)
        Matcher m2Matcher = Pattern.compile("2 -ème années Ingénieur").matcher(content);
        if (m2Matcher.find()) {
            return "M2";
        }

        // Check for L3 (3-ème années Licence)
        Matcher l3Matcher = Pattern.compile("3 -ème années Licence").matcher(content);
        if (l3Matcher.find()) {
            return "L3";
        }

        return null;
    }
}
