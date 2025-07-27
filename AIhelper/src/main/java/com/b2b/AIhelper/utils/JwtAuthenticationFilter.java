package com.b2b.AIhelper.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.b2b.AIhelper.entity.User;
import com.b2b.AIhelper.repository.UserRepository;
import com.b2b.AIhelper.utils.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Received Token: " + token);  // Debug log

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                Optional<User> userOptional = userRepository.findByEmail(email);

                if (userOptional.isPresent()) {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            userOptional.get().getEmail(), "", new ArrayList<>());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("User not found for email: " + email);  // Debug log
                }
            } else {
                System.out.println("Token is invalid");  // Debug log
            }
        }

        filterChain.doFilter(request, response);
    }
}

