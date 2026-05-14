package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.battery.main.data.dto.StoredFileDto;
import ru.battery.main.exceptions.ValidationException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {
    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    public StoredFileDto uploadFile(Long requestId, MultipartFile file) {
        validateFile(file);

        String filename = file.getOriginalFilename();
        String safeFilename = sanitizeFilename(filename);

        String objectKey = "requests/%d/%s".formatted(requestId, safeFilename);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return new StoredFileDto(bucket, objectKey, filename);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла перед загрузкой в S3", e);
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка загрузки файла в S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return UUID.randomUUID() + ".csv";
        }

        String cleaned = Paths.get(filename).getFileName().toString();
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");

        return UUID.randomUUID() + "_" + cleaned;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Файл не передан или пуст");
        }

        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.toLowerCase().endsWith(".csv")) {
            throw new ValidationException("Разрешены файлы только формата .csv");
        }
    }
}
