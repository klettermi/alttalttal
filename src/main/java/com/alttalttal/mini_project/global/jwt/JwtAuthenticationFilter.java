package com.alttalttal.mini_project.global.jwt;

import com.alttalttal.mini_project.domain.user.dto.LoginRequestDto;
import com.alttalttal.mini_project.domain.user.entity.UserRoleEnum;
import com.alttalttal.mini_project.global.security.UserDetailsImpl;
import com.alttalttal.mini_project.domain.user.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 로그인 및 JWT 생성
 */
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisService redisService) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        log.info("로그인 시도");
        try{
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        }catch (IOException e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getRole();

        //JWT Token 생성
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

        response.addHeader(JwtUtil.ACCESS_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);

        //Refresh Token 저장
        redisService.setValues(jwtUtil.substringToken(refreshToken), email, 60 * 60 * 24 * 30 * 1000L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("success", true);
        data.put("statusCode", HttpServletResponse.SC_OK);
        data.put("msg", "로그인 성공");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(data);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonString);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("success", false);
        data.put("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        data.put("msg", failed.getMessage());

        // 에러 메시지를 JSON 형식으로 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String errorJson = objectMapper.writeValueAsString(data);

        // 응답에 에러 메시지 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorJson);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
