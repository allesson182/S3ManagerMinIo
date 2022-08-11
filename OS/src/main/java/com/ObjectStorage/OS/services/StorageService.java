package com.ObjectStorage.OS.services;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class StorageService {
    @Value("${arquivos.raiz}")
    private String raiz;
    private MinioClient minio;

    @Autowired
    LocalStorageService localStorageService;

    public StorageService() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        this.minio = MinioClient.builder().endpoint("127.0.0.10",9000, false)
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    public List<Bucket> getBuckets() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return  minio.listBuckets();

    }

    public List<Item> getObjectsFromBucket(){
        List<Item> listaBucket = new ArrayList<>();
        minio.listObjects(ListObjectsArgs.builder().bucket("buckettest").build()).iterator().forEachRemaining(
                itemResult -> {
                    try {
                        listaBucket.add(itemResult.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
        return listaBucket;
    }

    public void uploadArquivo(MultipartFile arquivo, String bucketName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        File file = new File(this.raiz +"/arquivos/" + arquivo.getOriginalFilename());
        minio.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName).object(file.getName()).filename(file.getAbsolutePath()).build());

    }

    public void deletarArquivo(String nome) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minio.removeObject(RemoveObjectArgs.builder().bucket("buckettest").object(nome).build());
    }

    public void baixarArquivo(String nome) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        localStorageService.limpar(nome);
        File localFile = new File(this.raiz+"/arquivos/"+nome);
        minio.downloadObject(
                DownloadObjectArgs.builder().bucket("buckettest").object(nome).filename(localFile.getAbsolutePath()).build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartCreateBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minio.makeBucket(MakeBucketArgs.builder().bucket("buckettest").build());
    }
}
