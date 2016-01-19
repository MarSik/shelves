package org.marsik.elshelves.backend.security;

import org.springframework.security.core.GrantedAuthority;

public class SimpleAuthority implements GrantedAuthority {
    private final String authority;

    public SimpleAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
