package com.cinema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterDTO {

    @NotBlank(message = "Ten dang nhap khong duoc trong")
    @Size(min = 4, max = 50, message = "Ten dang nhap tu 4-50 ky tu")
    private String username;

    @NotBlank(message = "Email khong duoc trong")
    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "Mat khau khong duoc trong")
    @Size(min = 6, message = "Mat khau it nhat 6 ky tu")
    private String password;

    @NotBlank(message = "Ho ten khong duoc trong")
    private String fullName;
}