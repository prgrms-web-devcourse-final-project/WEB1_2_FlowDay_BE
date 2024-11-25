package org.example.flowday.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.handler.CustomAuthenticationFailureHandler;
import org.example.flowday.global.security.handler.CustomAuthenticationSuccessHandler;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MemberRepository memberRepository;


    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        setFilterProcessesUrl("/api/v1/members/login"); // 이 경로로 로그인 요청을 처리하도록 설정
        setUsernameParameter("loginId");  // 로그인 ID 파라미터 이름 설정
        setPasswordParameter("pw");       // 비밀번호 파라미터 이름 설정
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try {
            if (request.getContentType().equals("application/json")) {
                MemberDTO.LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(),
                                MemberDTO.LoginRequestDTO.class
                        );

                String username = loginRequest.getLoginId();
                String password = loginRequest.getPw();
                System.out.println(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

                // 인증 요청
                return authenticationManager.authenticate(authToken);
            } else {
                // request에서 username과 password 파라미터를 추출
                String username = obtainUsername(request); // getLoginId() 대신
                String password = obtainPassword(request); // getPw() 대신
                System.out.println(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

                // 인증 요청
                return authenticationManager.authenticate(authToken);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler(jwtUtil,memberRepository);
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        AuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
