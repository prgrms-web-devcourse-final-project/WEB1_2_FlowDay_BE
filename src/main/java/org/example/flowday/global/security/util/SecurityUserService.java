package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public SecurityUserService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        if(memberRepository.existsByLoginId(loginId)) {

            return new SecurityUser(memberRepository.findByLoginId(loginId).get());

        }
        return null;
    }

}
