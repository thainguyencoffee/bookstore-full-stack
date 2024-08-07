package com.bookstore.resourceserver.core.exception;

public class AmazonServiceS3Exception extends RuntimeException {

    public AmazonServiceS3Exception(String fileName, String folder) {
        super("Error uploading file " + fileName + " to folder " + folder);
    }

    public AmazonServiceS3Exception(String url) {
        super("Error deleting file with url " + url);
    }
}
