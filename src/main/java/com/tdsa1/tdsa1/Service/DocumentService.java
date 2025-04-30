package com.tdsa1.tdsa1.Service;
import com.tdsa1.tdsa1.Repository.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import com.tdsa1.tdsa1.Document.*;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {



    private final DocumentRepository documentRepository;

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final  ElasticsearchOperations elasticsearchOperations;

    private final PvRepository pvRepository;

    private final AnnanceRepository annanceRepository;

    private final PlanningRepository planningRepository;

    private final EmploiRepository emploiRepository;


    private final AutoDetectParser parser;

    private final ParseContext context;



    @Autowired
    public DocumentService(DocumentRepository documentRepository, ElasticsearchOperations elasticsearchOperations, PvRepository pvRepository, AnnanceRepository annanceRepository, PlanningRepository planningRepository, EmploiRepository emploiRepository) throws TikaException, IOException, SAXException {

        this.documentRepository = documentRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.pvRepository = pvRepository;
        this.annanceRepository = annanceRepository;
        this.planningRepository = planningRepository;
        this.emploiRepository = emploiRepository;


        TikaConfig tikaConfig = new TikaConfig(
                getClass().getClassLoader().getResourceAsStream("tika-config.xml")
        );
        this.parser = new AutoDetectParser(tikaConfig);
        TesseractOCRConfig ocr = new TesseractOCRConfig();
        ocr.setLanguage("eng+fra+ara");
        this.context = new ParseContext();
        context.set(TesseractOCRConfig.class, ocr);
    }

    public void upload(MultipartFile file, DocumenMetaData document) throws IOException {




        DocumentModel doc = new DocumentModel();

        doc.setTitle(document.getTitle());
        doc.setAuthor(document.getAuthor());
        doc.setContent(document.getContent());

        doc.setCreatedAt(document.getCreatedAt());


        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());

        // doc.setPublic(document.isPublic());

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.name();
//
//        doc.setOwner(currentUsername);

        //document set service



//        List<String> allowedExtensions = List.of("txt", "pdf", "docx");
//
//        if (!allowedExtensions.contains(extension)) {
//            return ResponseEntity.badRequest().body("Invalid file type.");
//        }





        String path = saveFile(file);

        doc.setFilePath(path);


        // Step 1: save file
        try {

            documentRepository.save(doc);           // Step 2: index in ES
        } catch (Exception e) {
            // rollback file save
            Files.deleteIfExists(Paths.get(path));
            throw new RuntimeException("Failed to index document", e);
        }






    }

    private String saveFile(MultipartFile file) {
        try {
            // Define the path to store files (local file system, or you could use cloud storage)
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




    public SearchHits<DocumentModel> searchDocument(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }

        Query titleQuery = MatchQuery.of(m -> m      //Query type
                .field("title")
                .query(word)
                .fuzziness("AUTO")
                .minimumShouldMatch("75%")
        )._toQuery();

        //blueprint for the bool query
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // Title query (fuzzy match)
        boolBuilder.should(titleQuery);



        Query authorQuery = TermQuery.of(t -> t
                .field("author")
                .value(word)
        )._toQuery();

        boolBuilder.should(authorQuery);


        try {
            LocalDate searchDate = LocalDate.parse(word, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Query dateQuery = TermQuery.of(t -> t
                    .field("createdAt")
                    .value(searchDate.format(DateTimeFormatter.ISO_DATE))
            )._toQuery();

            boolBuilder.should(dateQuery);
        } catch (DateTimeParseException e) {
            // Ignorer si ce n'est pas une date
        }

        boolBuilder.minimumShouldMatch("1");



        try {
//            Query ownerFilter = TermQuery.of(t -> t
//                    .field("owner")
//                   // .value(currentUsername)
//            )._toQuery();

            Query finalQuery = BoolQuery.of(b -> b
                            .must(boolBuilder.build()._toQuery()) // CritÃ¨res de recherche
                    //  .filter(ownerFilter)                 // Filtre par owner
            )._toQuery();



            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(finalQuery)
                    .build();//


            return elasticsearchOperations.search(
                    nativeQuery,
                    DocumentModel.class,
                    IndexCoordinates.of("documents")
            );
        } catch (Exception e) {
            throw new RuntimeException("Search failed for: " + word, e);
        }
    }

    public void indexFile(String indexName, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();

            try (InputStream stream = file.getInputStream()) {
                // Parse content and metadata

                BodyContentHandler handler = new BodyContentHandler(-1);


                Metadata metadata = new Metadata();
                parser.parse(stream, handler, metadata, context);
                String content = handler.toString();

                // Instantiate appropriate document
                BaseDocument doc;
                switch (indexName.toLowerCase()) {
                    case "pv":
                        doc = new PvDocument();
                        ((PvDocument) doc).setType(determinePvTypeFromContent(content,name));
                        break;
                    case "annance":
                        doc = new AnnanceDocument();
                        break;
                    case "emploi":
                        doc = new EmploiDocument();
                        break;
                    case "planning":
                        doc = new PlanningDocument();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown index: " + indexName);
                }

                // Populate shared fields
                String title = metadata.get(TikaCoreProperties.TITLE);
                doc.setTitle((title != null && !title.isBlank()) ? title : name);

                String author = metadata.get(TikaCoreProperties.CREATOR);
                doc.setAuthor((author != null && !author.isBlank()) ? author : "USTO");

                String dateStr = metadata.get(TikaCoreProperties.CREATED);
                if (dateStr != null && !dateStr.isBlank()) {
                    doc.setDateCreation(extractAndStandardizeDate(dateStr));
                }

                doc.setContent(content);
                doc.setFilePath(name);
                doc.setSize(file.getSize());
                doc.setPublic(true);

                // Save to correct repository
                if (doc instanceof PvDocument) {
                    pvRepository.save((PvDocument) doc);
                } else if (doc instanceof AnnanceDocument) {
                    annanceRepository.save((AnnanceDocument) doc);
                } else if (doc instanceof EmploiDocument) {
                    emploiRepository.save((EmploiDocument) doc);
                } else if (doc instanceof PlanningDocument) {
                    planningRepository.save((PlanningDocument) doc);
                }
            } catch (Exception e) {
                logger.error("Error indexing {} in {}: {}", name, indexName, e.getMessage());
            }
        }

    }

    private String determinePvTypeFromContent(String content,String name) {

        if(name.toLowerCase().contains("csf") || content.contains("csf")) {
            return"csf";
        }
        else if(name.toLowerCase().contains("csd") || content.contains("csd")){

            return "csd";
        }
        else {

            return "csf";
        }



    }


    public LocalDate extractAndStandardizeDate(String dateStr) {

        if (dateStr.isEmpty()) {
            return null;
        }

        try {
            // Case 1: PDF format "D:YYYYMMDD..."
            if (dateStr.startsWith("D:")) {
                String raw = dateStr.substring(2);
                String yyyyMMdd = raw.length() >= 8 ? raw.substring(0, 8) : raw;
                return LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE);
            }

            // Case 2: ISO-8601 (e.g., 2025-04-20T08:33:39Z)
            if (dateStr.contains("T")) {
                Instant instant = Instant.parse(dateStr);
                return instant.atZone(ZoneOffset.UTC).toLocalDate();
            }

            // Case 3: Textual format (e.g., "April 20, 2025")
            if (dateStr.matches(".*[A-Za-z]{3,}.*")) {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMMM d, yyyy")
                        .toFormatter(Locale.US);
                return LocalDate.parse(dateStr, formatter);
            }

            // Case 4: Simple ISO date (e.g., "2025-04-20")
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);

        } catch (DateTimeParseException e) {
            logger.warn("Unparseable date '{}', returning null", dateStr);
            return null;
        }
    }



}