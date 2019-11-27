package connectis.dbstorage;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/db")
public class ControllerDatabase {

    private DatabaseService databaseService;

    public ControllerDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostMapping("/files")
    public ResponseEntity<?> storeFiles(@RequestParam("file") MultipartFile file){
        return databaseService.storeFile(file);
    }

    @GetMapping("/files")
    public List<DatabaseFileManage> getFiles(){
        return databaseService.getFiles();
    }

    @GetMapping("/files/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id){
        return databaseService.download(id);
    }
    @DeleteMapping("/files/download/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id){
        return databaseService.delete(id);
    }
}
