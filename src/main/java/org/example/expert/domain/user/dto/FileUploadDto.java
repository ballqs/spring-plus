package org.example.expert.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadDto {
    private String fileName;
    private String convertFileName;
    private Long fileSize;
    private String contentType;
}
