package edu.nazaaaar.librarybackend.controller;

import edu.nazaaaar.librarybackend.dao.RegisterDto;
import edu.nazaaaar.librarybackend.dao.UserRepository;
import edu.nazaaaar.librarybackend.dao.LoginDto;
import edu.nazaaaar.librarybackend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("login")
    public ResponseEntity<Long> login(@RequestBody LoginDto loginDto){
        Optional<UserEntity> usr = userRepository.findByUsername(loginDto.getUsername());
        if (usr.isPresent()) {
            if (Objects.equals(usr.get().getPassword(), loginDto.getPassword())) {
                Long userId = usr.get().getId();
                return new ResponseEntity<>(userId, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(-1L, HttpStatus.UNAUTHORIZED); // Return -1 if login fails
    }


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setAddress(registerDto.getAddress());
        user.setPassword(registerDto.getPassword());
        user.setPib(registerDto.getPIB());

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }

}