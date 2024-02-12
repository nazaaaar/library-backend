package edu.nazaaaar.librarybackend.controller;

import edu.nazaaaar.librarybackend.dao.AuthResponseDTO;
import edu.nazaaaar.librarybackend.dao.RegisterDto;
import edu.nazaaaar.librarybackend.dao.UserRepository;
import edu.nazaaaar.librarybackend.dao.LoginDto;
import edu.nazaaaar.librarybackend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
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
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        Optional<UserEntity> usr = userRepository.findByUsername(loginDto.getUsername());
        if (usr.isPresent())
            if (Objects.equals(usr.get().getPassword(), loginDto.getPassword())) return new ResponseEntity<>("Success", HttpStatus.OK);
        return new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
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
        user.setPIB(registerDto.getPIB());

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }

}