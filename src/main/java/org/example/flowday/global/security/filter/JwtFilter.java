package org.example.flowday.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JwtFilter(final JwtUtil jwtUtil, final MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        System.out.println("authorization now");
        //토큰 소멸 시간 검증
        try {
            if(!jwtUtil.isExpired(token)) {
                Date expirationTime = jwtUtil.getExpiration(token);

                long remainingTime = expirationTime.getTime() - System.currentTimeMillis();
                long minutes = remainingTime / 60000;
                long seconds = (remainingTime % 60000) / 1000;

                String formattedTime = String.format("만료까지 남은 시간 : %02d:%02d", minutes, seconds);
                System.out.println(formattedTime);
            }
        }  catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println(e.getMessage());
            response.getWriter().print("Token Expired");
            return;
        }

        Long id = jwtUtil.getId(token);
        String category = jwtUtil.getCategory(token);

        if (!category.equals("accessToken")) {
            System.out.println(category);

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //userEntity를 생성하여 값 set
        Member member = memberRepository.findById(id).orElseThrow(
                MemberException.MEMBER_NOT_FOUND::getMemberTaskException
        );

        //UserDetails에 회원 정보 객체 담기
        SecurityUser customUserDetails = new SecurityUser(member);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}

