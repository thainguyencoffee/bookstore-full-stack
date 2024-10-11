package com.bookstorefullstack.bookstore.awss3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiMediaService {

    private final AmazonS3Service amazonS3Service;

    public List<String> uploadEverything(Class<?> uploadFor, Object identifier, List<MultipartFile> files, String type) {
        List<String> result = new ArrayList<>();
        files.forEach(part -> {
            String originalFilename = part.getOriginalFilename();
            if (originalFilename != null) {
                String extension = "";
                int i = originalFilename.lastIndexOf('.');
                if (i > 0) {
                    extension = originalFilename.substring(i);
                }

                String uniqueFilename = UUID.randomUUID() + extension;
                String folder = uploadFor.getSimpleName().toLowerCase() + "/" + identifier + "/" + type.toLowerCase() + "s" + "/";
                String url = amazonS3Service.uploadFile(part, folder + uniqueFilename);
                result.add(url);
            }
        });
        return result;
    }

    public void deleteEverything(List<String> urls) {
        assert urls != null && !urls.isEmpty();
        urls.forEach(amazonS3Service::deleteFile);
    }

    public void deleteEverything(String url) {
        assert url != null;
        amazonS3Service.deleteFile(url);
    }

}
