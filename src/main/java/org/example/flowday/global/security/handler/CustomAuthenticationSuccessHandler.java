package org.example.flowday.global.security.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.SecurityUser;
import org.example.flowday.global.security.util.oauth2.dto.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public CustomAuthenticationSuccessHandler(JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("Authentication Successful");

        String loginId = null;
        String role = null;
        Long id = null;

        // OAuth2 인증인 경우
        if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {

            CustomOAuth2User principal = (CustomOAuth2User) oauth2AuthenticationToken.getPrincipal();
            loginId = principal.getUsername();
            id = memberRepository.findIdByLoginId(loginId).get();
            role = principal.getAuthorities().iterator().next().getAuthority();

        }
        // 일반 로그인인 경우
        else if (authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            loginId = securityUser.getUsername();
            id = securityUser.getId();
            role = securityUser.getAuthorities().iterator().next().getAuthority();
        }


        System.out.println("JWT create");
        String accessToken = jwtUtil.createJwt(Map.of(
                        "category","accessToken",
                        "id",id,
                        "loginId",loginId,
                        "role", role),
                60 * 60 * 1000L); //1시간
        String refreshToken = jwtUtil.createJwt(Map.of(
                        "category","RefreshToken",
                        "id",id,
                        "loginId",loginId,
                        "role", role),
                60 * 60 * 100000L); //100시간

        Optional<Member> member = memberRepository.findByLoginId(loginId);
        member.get().setRefreshToken(refreshToken);
        memberRepository.save(member.get());

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", "Bearer " + refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Login successful\"}");
    }
}
