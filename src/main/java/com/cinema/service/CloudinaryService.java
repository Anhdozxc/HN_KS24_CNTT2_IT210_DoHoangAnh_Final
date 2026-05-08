package com.cinema.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final boolean configured;

    public CloudinaryService(@Value("${cloudinary.cloud-name:}") String cloudName,
                             @Value("${cloudinary.api-key:}") String apiKey,
                             @Value("${cloudinary.api-secret:}") String apiSecret) {
        configured = !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    // Upload anh len Cloudinary, tra ve URL cua anh
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        if (!configured) {
            throw new IllegalStateException("Chua cau hinh Cloudinary trong bien moi truong");
        }
        Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return result.get("secure_url").toString();
    }
}
