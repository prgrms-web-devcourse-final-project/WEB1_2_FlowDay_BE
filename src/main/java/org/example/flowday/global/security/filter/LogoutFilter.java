package org.example.flowday.global.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;

public class LogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public LogoutFilter(JwtUtil jwtUtil, MemberRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.memberRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();

        if (!requestUri.contains("/api/v1/members/logout")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String accessToken = token.split(" ")[1];

        Member member = memberRepository.findById(jwtUtil.getId(accessToken)).get();
        member.setRefreshToken(null);

        memberRepository.save(member);

        System.out.println("Security custom LogoutFilter");

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Logout successful\"}");
    }
}
