package com.bookstore.backend.core.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public class CloudinaryUtils {

    public static String uploadFile(MultipartFile file, Cloudinary cloudinary) throws IOException {
        String url = cloudinary.uploader()
                .upload(file.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                .get("url").toString();
        return url.substring(0, url.lastIndexOf("."));
    }

    public static String deleteFile(String publicId, Cloudinary cloudinary) throws IOException {
        return cloudinary.uploader()
                .destroy(publicId, Map.of("invalidate", true))
                .get("result")
                .toString();
    }

    // write a method to split url
    public static String convertUrlToPublicId(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static List<String> convertListMultipartFileToListUrl(List<MultipartFile> input, Cloudinary cloudinary) {
        return Optional.of(input)
                .filter(list -> !list.isEmpty())
                .map(multipartFiles -> {
                    var photos = new ArrayList<String>();
                    multipartFiles.forEach(multipartFile -> {
                        try {
                            photos.add(CloudinaryUtils.uploadFile(multipartFile, cloudinary));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return photos;
                }).orElseGet(() -> null);
    }

    public static String convertSingleMultipartFileToUrl(MultipartFile input, Cloudinary cloudinary) {
        return Optional.of(input)
                .map(multipartFile -> {
                    try {
                        return CloudinaryUtils.uploadFile(multipartFile, cloudinary);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).orElseGet(() -> null);
    }

}
