package com.effitrack.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.effitrack.server.constant.StringConst.AUTHORIZATION_HEADER;
import static com.effitrack.server.constant.StringConst.TOKEN_PREFIX;

@Component
public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtCore jwtCore;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        if (headerAuth != null && headerAuth.startsWith(TOKEN_PREFIX)) {
            jwt = headerAuth.substring(7);
        }

        if (jwt != null && jwtCore.validateToken(jwt)) {
            String username = jwtCore.getNameFromJwt(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
