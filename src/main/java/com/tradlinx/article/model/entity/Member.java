package com.tradlinx.article.model.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String userid;

    private String username;

    private String pw;

    private int point = 0;

    private String role;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "datetime")
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoggedAt;

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private List<Article> articleList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private List<Comments> commentsList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> set = new HashSet<>();
        if (this.role != null && this.role.equalsIgnoreCase("admin")) {
            set.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            set.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return set;
    }

    @Override
    public String getPassword() {
        return this.pw;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
