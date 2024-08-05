package com.bookstore.backend.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bookstore.backend.core.exception.AmazonServiceS3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
class AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Value("${digitalocean.spaces.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String key = folder + file.getOriginalFilename();
        var objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        try {
            amazonS3.putObject(bucketName, key, file.getInputStream(), objectMetadata);
            log.info("Upload media for " + folder + " successfully.");
        }
        catch (IOException | AmazonS3Exception e) {
            log.error("Error upload thumbnail for book.");
            throw new AmazonServiceS3Exception(fileName, folder);
        }
        return amazonS3.getUrl(bucketName, key).toString();
    }

    public void deleteFile(String url) {
        try {
            amazonS3.deleteObject(bucketName, cutURL(url, bucketName));
            log.info("Delete thumbnail with url {} successfully.", url);
        } catch (AmazonS3Exception e) {
            log.error("Error deleting thumbnail with url {}.", url);
            throw new AmazonServiceS3Exception(url);
        }
    }

    private String cutURL(String url, String bucketName) {
        String keyword = "/" + bucketName + "/";
        int startIndex = url.indexOf(keyword) + keyword.length();
        return url.substring(startIndex);
    }

}
