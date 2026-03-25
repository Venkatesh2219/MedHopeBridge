package com.medibridge.service;

import com.medibridge.dto.AuthDTO;
import com.medibridge.dto.UserDTO;
import com.medibridge.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);
    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);
    void resetPassword(AuthDTO.ForgotPasswordRequest request);
    void changePassword(Long userId, AuthDTO.ChangePasswordRequest request);
    UserDTO.Response getUserById(Long id);
    UserDTO.Response updateUser(Long id, UserDTO.UpdateRequest request);
    String uploadProfilePicture(Long userId, MultipartFile file);
    List<UserDTO.Response> getAllUsers();
    List<UserDTO.Response> searchUsers(String query);
    void deleteUser(Long id);
    User.UserStatus toggleUserStatus(Long id);
}
