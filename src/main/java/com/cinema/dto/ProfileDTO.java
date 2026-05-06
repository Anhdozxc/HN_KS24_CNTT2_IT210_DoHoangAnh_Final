package com.cinema.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ProfileDTO {
    private String fullName;
    private String phone;
    private String address;
    private MultipartFile avatarFile;  // File anh dai dien moi (co the null)
}