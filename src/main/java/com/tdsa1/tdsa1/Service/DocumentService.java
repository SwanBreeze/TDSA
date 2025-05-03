package com.tdsa1.tdsa1.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.tdsa1.tdsa1.Document.*;
import com.tdsa1.tdsa1.Repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DocumentService {


    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final DocumentRepository documentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private final PvRepository pvRepository;

    private final AnnanceRepository annonceRepository;

    private final PlanningRepository planningRepository;

    private final EmploiRepository emploiRepository;


    private final AutoDetectParser parser;

    private final ParseContext context;


    @Autowired
    public DocumentService(DocumentRepository documentRepository,
                           ElasticsearchOperations elasticsearchOperations,
                           PvRepository pvRepository,
                           AnnanceRepository annonceRepository,
                           PlanningRepository planningRepository,
                           EmploiRepository emploiRepository)
            throws TikaException, IOException, SAXException {

        this.documentRepository = documentRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.pvRepository = pvRepository;
        this.annonceRepository = annonceRepository;
        this.planningRepository = planningRepository;
        this.emploiRepository = emploiRepository;

        // ─── 1) Charger la config XML ────────────────────────────────
        AutoDetectParser autoParser;
        try (InputStream configStream = getClass()
                .getClassLoader()
                .getResourceAsStream("tika-config.xml")) {
            TikaConfig tikaConfig = new TikaConfig(configStream);
            autoParser = new AutoDetectParser(tikaConfig);
        }

        // ─── 2) (Optionnel) Reconfigurer manuellement le TesseractOCRParser ───
        for (org.apache.tika.parser.Parser p : autoParser.getParsers().values()) {
            if (p instanceof org.apache.tika.parser.ocr.TesseractOCRParser tessParser) {

                // On pointe sur le dossier contenant tesseract.exe
                tessParser.setTesseractPath("C:/Program Files/Tesseract-OCR");
                // Dossier des données de langue (traineddata)
                tessParser.setTessdataPath("C:/Program Files/Tesseract-OCR/tessdata");
                // Langues
                tessParser.setLanguage("eng+fra+ara");
                break;
            }
        }

        this.parser = autoParser;

// Config OCR supplémentaire
        TesseractOCRConfig ocrConfig = new TesseractOCRConfig();

// PAS “AUTO” mais un code valide, ici on laisse le mode par défaut (1),
// donc on peut même commenter la ligne suivante
        ocrConfig.setPageSegMode("1");

        // autres options (langue, page separator, etc.)
        // ocrConfig.setOutputType(TesseractOCRConfig.OUTPUT_TYPE.TEXT);

// injection dans le contexte
        this.context = new ParseContext();
        context.set(TesseractOCRConfig.class, ocrConfig);
    }


   /* public void upload(MultipartFile file, DocumenMetaData document) throws IOException {


        DocumentModel doc = new DocumentModel();

        doc.setTitle(document.getTitle());
        doc.setAuthor(document.getAuthor());
        doc.setContent(document.getContent());

        doc.setDateCreation(document.getDateCreation());


        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());

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


    }*/

    // the upload for the controller
    public void upload(MultipartFile file, DocumenMetaData document) throws IOException {
        // Create a BaseDocument or DocumentModel based on your design
        BaseDocument baseDocument = new BaseDocument();

        // Call the helper method that handles the actual upload logic
        uploadDocument(file, document, baseDocument);
    }


    //General Upload Method
    public void uploadDocument(MultipartFile file, DocumenMetaData document, BaseDocument baseDocument) throws IOException {
        // Common properties for all documents

        baseDocument.setTitle(document.getTitle());
        baseDocument.setAuthor(document.getAuthor());
        baseDocument.setContent(document.getContent());
        baseDocument.setDateCreation(document.getDateCreation());
        baseDocument.setFileName(file.getOriginalFilename());
        baseDocument.setFileType(file.getContentType());
        baseDocument.setFileSize(file.getSize());

        // Save file to disk and set file path
        String path = saveFile(file);
        baseDocument.setFilePath(path);

        try {
            // Save the document to the repository (and index in Elasticsearch automatically)
            documentRepository.save(baseDocument);
        } catch (Exception e) {
            // Rollback file save on error
            Files.deleteIfExists(Paths.get(path));
            throw new RuntimeException("Failed to index document", e);
        }
    }
    // Upload for the Index Pv

    public void uploadPvDocument(MultipartFile file, DocumenMetaData document) throws IOException {
        PvDocument pvDocument = new PvDocument();
        pvDocument.setType(document.getType()); // Set type (csf or csd)
        uploadDocument(file, document, pvDocument);
    }

    // Upload for the Index Annonce
    public void uploadAnnonceDocument(MultipartFile file, DocumenMetaData document) throws IOException {
        AnnanceDocument annanceDocument = new AnnanceDocument();
        uploadDocument(file, document, annanceDocument);
    }

    //Upload for the Index Planning
    public void uploadPlanningDocument(MultipartFile file, DocumenMetaData document) throws IOException {
        PlanningDocument planningDocument = new PlanningDocument();
        uploadDocument(file, document, planningDocument);
    }

    // Upload for the Index Emploi
    public void uploadEmploiDocument(MultipartFile file, DocumenMetaData document) throws IOException {
        EmploiDocument emploiDocument = new EmploiDocument();
        uploadDocument(file, document, emploiDocument);
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

    // Method General For Search

    public <T extends BaseDocument> SearchHits<T> searchDocument(
            String word,
            String indexName,
            Class<T> documentClass,
            boolean includeExtraFields
    ) {
        if (word == null || word.isEmpty()) return null;

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // Fuzzy match on title
        boolBuilder.should(MatchQuery.of(m -> m
                .field("title")
                .query(word)
                .fuzziness("AUTO")
                .minimumShouldMatch("75%")
        )._toQuery());

        // Exact match on author
        boolBuilder.should(TermQuery.of(t -> t
                .field("author")
                .value(word)
        )._toQuery());

        // Match on dateCreation if parsable
        try {
            LocalDate searchDate = LocalDate.parse(word, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            boolBuilder.should(TermQuery.of(t -> t
                    .field("dateCreation")
                    .value(searchDate.format(DateTimeFormatter.ISO_DATE))
            )._toQuery());
        } catch (DateTimeParseException e) {
            // Ignore
        }

        // Extra fields for PvDocument only (csf and csd)
        if (includeExtraFields) {
            boolBuilder.should(MatchQuery.of(m -> m.field("type").query(word))._toQuery());
            boolBuilder.should(MatchQuery.of(m -> m.field("type").query(word))._toQuery());
        }

        boolBuilder.minimumShouldMatch("1");

        try {
            Query finalQuery = BoolQuery.of(b -> b.must(boolBuilder.build()._toQuery()))._toQuery();

            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(finalQuery)
                    .build();

            return elasticsearchOperations.search(
                    nativeQuery,
                    documentClass,
                    IndexCoordinates.of(indexName)
            );
        } catch (Exception e) {
            throw new RuntimeException("Search failed for: " + word, e);
        }
    }

    // Search For Index Pv
    public SearchHits<PvDocument> searchPvDocument(String word) {
        return searchDocument(word, "Pv", PvDocument.class, true);
    }

    //Search For Index Annonce

    public SearchHits<AnnanceDocument> searchAnnanceDocument(String word) {
        return searchDocument(word, "Annonce", AnnanceDocument.class, false);
    }

    //Search For Index Planning
    public SearchHits<PlanningDocument> searchPlanningDocument(String word) {
        return searchDocument(word, "Planning", PlanningDocument.class, false);
    }

    // Search For Index Emploi
    public SearchHits<EmploiDocument> searchEmploiDocument(String word) {
        return searchDocument(word, "Emploi", EmploiDocument.class, false);
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
                    case "Pv":
                        doc = new PvDocument();
                        ((PvDocument) doc).setType(determinePvTypeFromContent(content, name));
                        break;
                    case "Annonce":
                        doc = new AnnanceDocument();
                        break;
                    case "Emploi":
                        doc = new EmploiDocument();
                        break;
                    case "Planning":
                        doc = new PlanningDocument();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown index: " + indexName);
                }

                // Populate shared fields
                String title = metadata.get(TikaCoreProperties.TITLE);

                assert name != null;
                if (!name.toLowerCase().contains(indexName)) {

                    String newname = indexName + "-" + name;

                    doc.setTitle(newname);

                } else if (name.equals(".pdf")) {


                    doc.setTitle((title != null && !title.isBlank()) ? title : indexName + name);

                } else {

                    doc.setTitle(name);

                }


                String author = metadata.get(TikaCoreProperties.CREATOR);
                if (author == null || author.isBlank() || author.equalsIgnoreCase("Unknown Creator")) {
                    doc.setAuthor("USTO");
                } else {
                    doc.setAuthor(author);
                }

                String dateStr = metadata.get(TikaCoreProperties.CREATED);

                LocalDate extractedDate = extractAndStandardizeDate(dateStr);
                if (extractedDate != null) {
                    doc.setDateCreation(extractedDate);

                    System.out.println(extractedDate);

                } else {

                    logger.warn("Date non trouvée ou invalide pour le fichier: {}", name);
                    //doc.setDateCreation(LocalDate.now());
                }

                doc.setContent(content);
                doc.setFilePath(name);


                doc.setFileSize(file.getSize());
                doc.setPublic(true);

                // Save to correct repository
                if (doc instanceof PvDocument) {
                    pvRepository.save((PvDocument) doc);
                } else if (doc instanceof AnnanceDocument) {
                    annonceRepository.save((AnnanceDocument) doc);
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

    private String determinePvTypeFromContent(String content, String name) {

        if (name.toLowerCase().contains("csf") || content.toLowerCase().contains("csf")) {
            return "csf";
        } else if (name.toLowerCase().contains("csd") || content.contains("csd")) {

            return "csd";
        } else {

            return "csf";
        }


    }


    public LocalDate extractAndStandardizeDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || dateStr.equalsIgnoreCase("unknown")) {
            return null;
        }

        try {
            if (dateStr.startsWith("D:")) {
                String raw = dateStr.substring(2);
                String yyyyMMdd = raw.length() >= 8 ? raw.substring(0, 8) : raw;
                return LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE);
            }

            if (dateStr.contains("T")) {
                Instant instant = Instant.parse(dateStr);
                return instant.atZone(ZoneOffset.UTC).toLocalDate();
            }

            if (dateStr.matches(".*[A-Za-z]{3,}.*")) {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMMM d, yyyy")
                        .toFormatter(Locale.US);
                return LocalDate.parse(dateStr, formatter);
            }

            // Nouveau : format ISO classique
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException ignored) {
            }

            // Nouveau : format avec heure
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDate.parse(dateStr, formatter);

        } catch (DateTimeParseException e) {
            logger.warn("Unparseable date '{}'", dateStr);
            return null;
        }
    }


}