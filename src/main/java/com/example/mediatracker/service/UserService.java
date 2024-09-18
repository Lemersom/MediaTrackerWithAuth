package com.example.mediatracker.service;

import com.example.mediatracker.dto.UserResponseDTO;
import com.example.mediatracker.model.UserModel;
import com.example.mediatracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> findAllUser(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> userListPage = userRepository.findAll(pageable);

        return userListPage.getContent().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole()))
                .toList();
    }

    public Optional<UserResponseDTO> findUserById(Long requestedId) {
        Optional<UserModel> user = userRepository.findById(requestedId);
        if(user.isEmpty()) {
            return Optional.empty();
        }

        UserResponseDTO userResponse = new UserResponseDTO(user.get().getId(), user.get().getUsername(), user.get().getRole());

        return Optional.of(userResponse);
    }

}
