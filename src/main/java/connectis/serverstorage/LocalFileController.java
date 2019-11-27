package connectis.serverstorage;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class LocalFileController {

    private LocalFileService localFileService;

    public LocalFileController(LocalFileService localFileService) {
        this.localFileService = localFileService;
    }

    @GetMapping("/files")
    public List<LocalFile> getFiles(){
        return localFileService.getFiles();
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file){
        return localFileService.uploadFile(file);
    }

    @GetMapping("/files/downloads/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename){
        return localFileService.getFile(filename);
    }

    @DeleteMapping("/files/delete/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename){
        return localFileService.deleteFile(filename);
    }

}
