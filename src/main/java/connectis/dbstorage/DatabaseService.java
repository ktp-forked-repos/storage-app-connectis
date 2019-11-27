package connectis.dbstorage;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class DatabaseService {
    public static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private DatabaseRepository databaseRepository;

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public ResponseEntity<List<DatabaseFile>> getAllFiles() {
        return new ResponseEntity<>(databaseRepository.findAll(), HttpStatus.OK);
    }

    public List<DatabaseFileManage> getFiles() {
        List<DatabaseFile> files = databaseRepository.findAll();
        List<DatabaseFileManage> results = new ArrayList<>();

        files.forEach(f -> {
            String downloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/db/files/download/")
                    .path(f.getId())
                    .toUriString();


            String deleteUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/db/files/delete/")
                    .path(f.getId())
                    .toUriString();

            DatabaseFileManage file = new DatabaseFileManage
                    .Builder()
                    .downloadUri(downloadUri)
                    .deleteUri(deleteUri)
                    .filename(f.getFileName())
                    .size((long) f.getData().length)
                    .fileType(f.getFileType())
                    .build();

            results.add(file);

        });
        return results;
    }


    public ResponseEntity<?> storeFile(MultipartFile file) {
        String filename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        if (Strings.isBlank(filename)) {
            logger.error("Cannot receive file from input");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("Cannot receive file from input: {}", filename);

        // todo ?? wtf?? Long storeDate = new Date().getTime();
        Date date = new Date();

        DatabaseFile databaseFile = new DatabaseFile();
        databaseFile.setFileName(filename);
        databaseFile.setFileType(file.getContentType());
        databaseFile.setDate(date);

        try {
            logger.info("Try to get data from file: {}", filename);
            databaseFile.setData(file.getBytes());
        } catch (IOException e) {
            logger.error("Error while getting data from file:{},{}", filename, e.getMessage());

            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
        logger.info("Store file to database: {}", filename);
        return new ResponseEntity<>(databaseRepository.save(databaseFile), HttpStatus.CREATED);
    }

    public ResponseEntity<Resource> download(String id) {
        DatabaseFile file = databaseRepository.getOne(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFileName() + "\"")
                .header("pozdrowionka", "Pozdrawia connectis")
                .body(new ByteArrayResource(file.getData()));

    }

    public ResponseEntity<?> delete(String id) {
        databaseRepository.deleteById(id);

        DatabaseFile file = databaseRepository.getOne(id);
        if (file != null) {
            return new ResponseEntity<>("File not deleted", HttpStatus.CONFLICT);
        }

        logger.info("File \"{}\" deleted succesfully!");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}