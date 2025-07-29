package com.example.blob;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blob")
public class BlobController {

    private final BlobService blobService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws Exception {
        String url = blobService.uploadFile(file);
        return ResponseEntity.ok(url); // 필요시 SAS 붙인 URL로 가공 가능
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String blobName) {
        byte[] data = blobService.downloadFile(blobName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + blobName + "\"")
                .body(data);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String blobName) {
        blobService.deleteFile(blobName);
        return ResponseEntity.noContent().build();
    }
}
