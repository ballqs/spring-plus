package org.example.expert.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.dto.FileDownloadDto;
import org.example.expert.domain.user.dto.FileUploadDto;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.entity.UserProfile;
import org.example.expert.domain.user.repository.UserProfileRepository;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final AmazonS3 amazonS3;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // file upload
    @Transactional
    public void saveFile(Long userId , MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (file.isEmpty()) {
            return;
        }

        FileUploadDto fileUploadDto = storeFile(file);

        UserProfile userProfile = new UserProfile(fileUploadDto.getFileName() , fileUploadDto.getConvertFileName() , user);
        userProfileRepository.save(userProfile);

        awsS3Upload(file , fileUploadDto);
    }

    @Async
    protected void awsS3Upload(MultipartFile file , FileUploadDto fileUploadDto) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileUploadDto.getFileSize());
        metadata.setContentType(fileUploadDto.getContentType());
        amazonS3.putObject(bucket, fileUploadDto.getConvertFileName(), file.getInputStream(), metadata);
    }

    // file download
    public FileDownloadDto downloadImage(String originalFilename) {
        log.info("test : {}" , amazonS3.getUrl(bucket, originalFilename));
        UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, originalFilename));
        String contentDisposition = "attachment; filename=\"" +  UriUtils.encode(originalFilename, "UTF-8") + "\"";
        return new FileDownloadDto(urlResource, contentDisposition);
    }

    // file delete
    @Transactional
    public void deleteImage(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("UserProfile not found"));

        userProfileRepository.delete(userProfile);

        awsS3Delete(userProfile.getConvertFileName());
    }

    @Async
    protected void awsS3Delete(String convertFileName) {
        amazonS3.deleteObject(bucket, convertFileName);
    }

    private FileUploadDto storeFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();           // 사용자가 업로드한 파일이름
        String convertFileName = createStoreFileName(fileName);   // 서버에 저장하는 파일명(uu아이디+.+확장자명)
        Long fileSize = file.getSize();                                 // 파일 크기
        String contentType = file.getContentType();                     // 파일 contentType
        return new FileUploadDto(fileName, convertFileName , fileSize , contentType);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString(); // 서버에 저장하는 파일명(uu아이디)
        return uuid + "." + ext;                    // ext : 확장자명
    }

    private String extractExt(String originalFilename) {        // 확장자명 꺼내기
        int pos = originalFilename.lastIndexOf(".");        // 위치를 가져온다.
        return originalFilename.substring(pos + 1);   // . 다음에 있는 확장자명 꺼냄
    }
}
