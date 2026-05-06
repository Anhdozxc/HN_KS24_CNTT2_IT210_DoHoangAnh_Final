package com.cinema.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        // Khoi tao Cloudinary voi thong tin tai khoan
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dldtbezyz",
                "api_key",    "297592273111535",
                "api_secret", "NhvAc0v4kW7IHWI_A2dwX8CLQF0"
        ));
    }

    // Upload anh len Cloudinary, tra ve URL cua anh
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return result.get("secure_url").toString();
    }
}