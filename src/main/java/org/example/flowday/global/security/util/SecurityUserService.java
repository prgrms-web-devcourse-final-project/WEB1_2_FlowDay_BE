package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public SecurityUserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        System.out.println("Looking for user with loginId: " + loginId);

        try {
            Map<String,Object> result = memberRepository.findSecurityInfoByLoginId(loginId).get();

            System.out.println("find successful");

            Member member = new Member();
            member.setId(Long.parseLong(result.get("id").toString()));
            member.setLoginId(loginId);
            member.setRole((Role) result.get("role"));
            member.setPw((String) result.get("pw"));

            return new SecurityUser(member);
        } catch (RuntimeException e) {
            throw new UsernameNotFoundException("Member not found with loginId: " + loginId);
        }
    }
}
