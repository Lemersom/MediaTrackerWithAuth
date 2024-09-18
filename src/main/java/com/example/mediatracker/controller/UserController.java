package com.example.mediatracker.controller;

import com.example.mediatracker.dto.UserResponseDTO;
import com.example.mediatracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", defaultValue = "20") Integer size) {
        List<UserResponseDTO> userResponseListPage = userService.findAllUser(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseListPage);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestedId) {
        Optional<UserResponseDTO> user = userService.findUserById(requestedId);
        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

}
