package com.example.blob;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlobService {

    private final BlobContainerClient blobContainerClient;

    public String uploadFile(MultipartFile file) throws Exception {
        // user 디렉토리에 저장
        String blobName = "user/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);

            // 브라우저에서 바로 열리도록 Content-Type 및 Content-Disposition 설정
            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(file.getContentType())   // 이미지 타입 그대로
                    .setContentDisposition("inline");        // 다운로드 대신 바로 표시
            blobClient.setHttpHeaders(headers);
        }

        // SAS 없이 Public Blob URL 반환
        return blobClient.getBlobUrl();
    }

    public byte[] downloadFile(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        return blobClient.downloadContent().toBytes();
    }

    public void deleteFile(String blobName) {
        blobContainerClient.getBlobClient(blobName).delete();
    }
}
