package ru.battery.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadEventDto {
    private Long userId;

    private Long requestId;

    private String bucket;

    private String objectKey;

    private String fileName;

    private String nameModel;
}
