package io.metersphere.sql.service.common.impl;

import io.metersphere.sdk.file.MinioRepository;
import io.metersphere.sql.service.common.JarPackagesManageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.io.InputStream;
import io.minio.PutObjectArgs;

@Service
public class JarPackagesManageServiceImpl implements JarPackagesManageService {

    @Resource
    MinioClient minioClient;

    private final Lock lock = new ReentrantLock();

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null.");
        }

        // 加个锁，防止并发问题
        lock.lock();

        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String fileNameWithTimestamp = originalFilename.replace(".jar", "_" + timestamp + ".jar");

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(MinioRepository.JAR_BUCKET)
                            .object(fileNameWithTimestamp)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } finally {
            lock.unlock();
        }

        return fileNameWithTimestamp;
    }

    @Override
    public byte[] getFile(String fileName) throws Exception {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(MinioRepository.JAR_BUCKET)
                        .object(fileName)
                        .build()
        )) {
            return inputStream.readAllBytes();
        }
    }

}
