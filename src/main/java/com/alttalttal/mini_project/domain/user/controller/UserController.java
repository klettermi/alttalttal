package com.alttalttal.mini_project.domain.user.controller;

import com.alttalttal.mini_project.global.dto.MessageResponseDto;
import com.alttalttal.mini_project.domain.user.dto.SignUpRequestDto;
import com.alttalttal.mini_project.domain.user.dto.UserInfoResponseDto;
import com.alttalttal.mini_project.global.jwt.JwtUtil;
import com.alttalttal.mini_project.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@CrossOrigin(exposedHeaders = "*")
@RestController
public class UserController {
    private final UserService userService;
    private  final JwtUtil jwtUtil;
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;

    }

    @PostMapping("/users/signup")
    public ResponseEntity<MessageResponseDto> createUser(@RequestBody SignUpRequestDto signUpRequestDto) {
        return userService.createUser(signUpRequestDto);
    }

//    @PostMapping("/users/login")
//    public ResponseEntity<MessageResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
//        return userService.loginUser(loginRequestDto, response);
//    }

    @GetMapping("/users/logout")
    public ResponseEntity<MessageResponseDto> logoutUser(@RequestHeader(JwtUtil.ACCESS_HEADER) String accessToken, @RequestHeader(JwtUtil.REFRESH_HEADER) String refreshToken){
        return userService.logoutUser(accessToken, refreshToken);
    }


    @GetMapping("/users/info")
    public UserInfoResponseDto userInfo(HttpServletRequest request, HttpServletResponse response){
        return userService.userInfo(request, response);
    }
}
