package com.bookstore.backend.core.cloudinary;

import com.bookstore.backend.core.config.BookstoreProperties;
import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The type Cloudinary config.
 */
@Configuration
public class CloudinaryConfig {

    /**
     * @param bookstoreProperties
     * @return
     */
    @Bean
    public Cloudinary cloudinary(BookstoreProperties bookstoreProperties){
        return new Cloudinary(Map.of(
                "cloud_name", bookstoreProperties.cloudinary().cloudName(),
                "api_key", bookstoreProperties.cloudinary().apiKey(),
                "api_secret", bookstoreProperties.cloudinary().apiSecret()
        ));
    }

}
