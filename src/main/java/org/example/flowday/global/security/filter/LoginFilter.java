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
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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

    private final CustomAuthenticationFailureHandler failureHandler;


    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, MemberRepository memberRepository, CustomAuthenticationFailureHandler failureHandler) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.failureHandler = failureHandler;
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
        } catch (AuthenticationException e) {
            System.out.println("커스텀 필터 실행");
            failureHandler.onAuthenticationFailure(request, response, e);

            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("Authentication Successful");

        // JWT 생성 및 응답 설정
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        String username = securityUser.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        System.out.println("JWT create");
        String accessToken = jwtUtil.createJwt(Map.of(
                        "category","accessToken",
                        "id",securityUser.getId(),
                        "loginId",username,
                        "role", role),
                60 * 60 * 1000L); //1시간
        String refreshToken = jwtUtil.createJwt(Map.of(
                        "category","RefreshToken",
                        "loginId",username,
                        "role", role),
                60 * 60 * 100000L); //100시간

        Optional<Member> member = memberRepository.findByLoginId(username);
        member.get().setRefreshToken(refreshToken);
        memberRepository.save(member.get());

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", "Bearer " + refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Login successful\"}");
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
    }
}
