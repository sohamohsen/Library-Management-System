package com.soha.librarymanagement.security;

import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SystemUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        SystemUser u = isEmail(identifier)
                ? userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + identifier))
                : userRepository.findByUsername(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username: " + identifier));

        // النقطة الذهبية: بنرجّع UserDetails بـ getUsername() = username الحقيقي
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())      // << username هنا
                .password(u.getPassword())
                .disabled(!u.isEnabled())
                .authorities(u.getRole().getAuthorities()) // حسب عندك
                .build();
    }

    private boolean isEmail(String s) {
        return s != null && s.contains("@");
    }
}
