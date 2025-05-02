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


    public void upload(MultipartFile file, DocumenMetaData document) throws IOException {


        DocumentModel doc = new DocumentModel();

        doc.setTitle(document.getTitle());
        doc.setAuthor(document.getAuthor());
        doc.setContent(document.getContent());

        doc.setDateCreation(document.getDateCreation());


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
                            .must(boolBuilder.build()._toQuery()) // Critères de recherche
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
                        ((PvDocument) doc).setType(determinePvTypeFromContent(content, name));
                        break;
                    case "annonce":
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