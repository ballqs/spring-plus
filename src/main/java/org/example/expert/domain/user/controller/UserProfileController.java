package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.FileDownloadDto;
import org.example.expert.domain.user.dto.request.FileRequestDto;
import org.example.expert.domain.user.service.UserProfileService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/upload")
    public void fileUpload(@AuthenticationPrincipal AuthUser authUser, @RequestParam MultipartFile file) throws IOException {
        userProfileService.saveFile(Long.parseLong(authUser.getUserId()) , file);
    }

    @PostMapping("/download")
    public ResponseEntity<UrlResource> fileDownload(@RequestBody FileRequestDto fileRequestDto) {
        FileDownloadDto fileDownloadDto = userProfileService.downloadImage(fileRequestDto.getFileName());

        // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDownloadDto.getContentDisposition())
                .body(fileDownloadDto.getUrlResource());
    }

    @DeleteMapping("/delete")
    public void fileDelete(@AuthenticationPrincipal AuthUser authUser) {
        userProfileService.deleteImage(Long.parseLong(authUser.getUserId()));
    }
}
