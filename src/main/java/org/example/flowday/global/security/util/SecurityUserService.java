package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public SecurityUserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Looking for user with loginId: " + loginId);
        Map<String,Object> result = memberRepository.findSecurityInfoByLoginId(loginId).orElseThrow(() -> {

            System.out.println("User not found with loginId: " + loginId);

            return new UsernameNotFoundException("User not found with loginId");

        });
        System.out.println("find successful");

        Member member = new Member();
        member.setId((Long) result.get("id"));
        member.setLoginId(loginId);
        member.setPw(String.valueOf(result.get("pw")));
        member.setRole((Role) result.get("role"));

        return new SecurityUser(member);
    }
}
