package com.gully.ggos.security;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class SecurityUser implements UserDetails {
    private final UUID userId;
    private final UUID orgId;
    private final String username;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(
        UUID userId,
        UUID orgId,
        String username,
        String password,
        boolean active,
        Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.orgId = orgId;
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }
}
