package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "system_users")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = {"role", "password"})
@EqualsAndHashCode(exclude = {"role"})
public class SystemUser implements UserDetails, Principal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    private String phone;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreatedDate
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "create_by", nullable = false)
    private Integer createBy;

    @Override public String getName() { return username; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities(); // يعمل لأن Role بتاعك فيه الميثود
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
