package com.workintech.s19d2.service;

import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(MemberRepository memberRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String rawPassword) {

        // email var mı?
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with given email already exist");
        }

        // şifre encode et
        String encoded = passwordEncoder.encode(rawPassword);

        // ADMIN rolünü bul
        Role adminRole = roleRepository.findByAuthority("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        // yeni member hazırla
        Member newMember = new Member();
        newMember.setEmail(email);
        newMember.setPassword(encoded);
        newMember.setRoles(Collections.singletonList(adminRole));

        // veritabanına kaydet
        return memberRepository.save(newMember);
    }
}
