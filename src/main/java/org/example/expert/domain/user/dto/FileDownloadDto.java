package org.example.expert.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.UrlResource;

@Getter
@AllArgsConstructor
public class FileDownloadDto {
    private UrlResource urlResource;
    private String contentDisposition;
}
