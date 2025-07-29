package com.example.blob;

import com.azure.storage.blob.*;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class BlobStorageConfig {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Bean
    public BlobContainerClient blobContainerClient() {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        String endpoint = String.format("https://%s.blob.core.windows.net", accountName);

        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .credential(credential)
                .endpoint(endpoint)
                .buildClient();

        return serviceClient.getBlobContainerClient(containerName);
    }

    @Bean
    public StorageSharedKeyCredential storageSharedKeyCredential() {
        return new StorageSharedKeyCredential(accountName, accountKey);
    }
}
