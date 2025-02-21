package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String role;
    private String username;
    private String oldPassword;
    private String newPassword;
}
