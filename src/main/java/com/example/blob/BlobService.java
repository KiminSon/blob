package com.example.blob;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.sas.*;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.common.sas.SasProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlobService {

    private final BlobContainerClient blobContainerClient;
    private final StorageSharedKeyCredential credential;

    public String uploadFile(MultipartFile file) throws Exception {
        String blobName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);

            // ✅ HTTP 헤더 설정
            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(file.getContentType())       // ex: image/jpeg
                    .setContentDisposition("inline");            // 브라우저에서 바로 열기

            blobClient.setHttpHeaders(headers);
        }

        return generateSasUrl(blobClient);
    }


    public byte[] downloadFile(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        return blobClient.downloadContent().toBytes();
    }

    public void deleteFile(String blobName) {
        blobContainerClient.getBlobClient(blobName).delete();
    }

    private String generateSasUrl(BlobClient blobClient) {
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusYears(10); // 10년 후 만료

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setStartTime(OffsetDateTime.now())
                .setProtocol(SasProtocol.HTTPS_ONLY)
                .setContainerName(blobClient.getContainerName())
                .setBlobName(blobClient.getBlobName());

        String sasToken = blobClient.generateSas(sasValues);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }
}
