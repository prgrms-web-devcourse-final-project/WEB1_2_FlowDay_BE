package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public SecurityUserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Looking for user with loginId: " + loginId);
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> {
            System.out.println("User not found with loginId: " + loginId);
            return new UsernameNotFoundException("User not found with loginId: " + loginId);
        });
        System.out.println("find successful");
        return new SecurityUser(member);
    }
}
