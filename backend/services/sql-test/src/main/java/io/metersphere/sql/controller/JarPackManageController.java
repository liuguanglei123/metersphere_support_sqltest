package io.metersphere.sql.controller;

import io.metersphere.sql.service.common.JarPackagesManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/sql/jar-package")
@RestController
public class JarPackManageController {

    @Autowired
    private JarPackagesManageService fileUploadService;

    @PostMapping(value="/upload")
    public ResponseEntity<String> uploadJarFile(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getOriginalFilename().endsWith(".jar")) {
                return ResponseEntity.badRequest().body("Only JAR files are allowed.");
            }

            String fileName = fileUploadService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping(value="/download/{filename}")
    public ResponseEntity<Object> downloadJarFile(@PathVariable String filename) {
        try {
            byte[] fileBytes = fileUploadService.getFile(filename);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving file: " + e.getMessage());
        }
    }

}