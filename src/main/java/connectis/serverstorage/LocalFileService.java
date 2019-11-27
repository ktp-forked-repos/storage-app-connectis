package connectis.serverstorage;


import org.apache.logging.log4j.util.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
@Service
public class LocalFileService {

    public static final Logger logger = LoggerFactory.getLogger(LocalFileService.class);

    private ServletContext servletContext;
    private String uploads;

    public LocalFileService(ServletContext servletContext) {
        this.servletContext = servletContext;
        createDirectory();
    }

    private void createDirectory() {
       uploads = servletContext.getRealPath("/uploads/");
        //uploads = "C:/uploads/";


        if (Strings.isBlank(uploads)) {
            logger.error("Cannot create set file path!");
        }

        Path path = Paths.get(uploads);

        if (Files.notExists(path)) {

            try {
                logger.info("Try to create directory: {}", uploads);
                Files.createDirectory(path);
            } catch (IOException e) {
                logger.error("Cannot create directory from path: {}, error {}", path, e.getMessage());
            }

        }
        logger.info("Directory created:{}", uploads);
    }

    public ResponseEntity<Resource> getFile(String filename) {
        Path path = Paths.get(uploads + filename);
        Resource resource;
        try {
            logger.info("Try to get file from: {}", uploads);
            resource = new UrlResource(path.toUri());

        } catch (MalformedURLException e) {
            logger.error("Failed to get file from: {}. Error {}", uploads, e.getMessage());
            return ResponseEntity.notFound().build();
        }
        File targetFile;
        try {
            targetFile = resource.getFile();
        } catch (IOException e) {
            logger.error("Cannot get resource from: {}. Error {}", resource.getFilename(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            logger.warn("Cannot content type from file: {}.", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + targetFile.getName() + "\"")
                .contentLength(targetFile.length())
                .body(resource);
    }

    public List<LocalFile> getFiles() {
        Stream<Path> files;
        try {
            files = Files
                    .walk(Paths.get(uploads))
                    .filter(Files::isRegularFile);
        } catch (IOException e) {
            logger.error("Cannot get files: {}", e.getMessage());
            return null;
        }
        List<LocalFile> localFiles = new ArrayList<>();
        files.forEach(f -> {
            BasicFileAttributes bs;
            try {
                bs = Files.readAttributes(f.toAbsolutePath(), BasicFileAttributes.class);
            } catch (IOException e) {
                logger.error("Cannot get file attributes: {}", e.getMessage());
                return;
            }


            String downloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/files/download")
                    .path(f.getFileName().toString())
                    .toUriString();

            String deleteUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/files/delete")
                    .path(f.getFileName().toString())
                    .toUriString();

            LocalFile localFile = new LocalFile();
            localFile.setName(f.getFileName().toString());
            localFile.setCreationTime(bs.creationTime().toString());
            localFile.setDownloadUri(downloadUri);
            localFile.setDeleteUri(deleteUri);
            localFile.setSize(bs.size());
            localFile.setLastModified(bs.lastModifiedTime().toString());
            try {
                localFile.setFileType(Files.probeContentType(f));
            } catch (IOException e) {
                logger.error("Cannot get file type:{}", e.getMessage());
            }
        });
        return localFiles;
    }
}