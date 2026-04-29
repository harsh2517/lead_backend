package com.accountooze.config;


import java.io.IOException;
import java.util.List;

import com.accountooze.model.LoginHistory;
import com.accountooze.repo.LoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String requestURI = request.getServletPath();

        if (requestURI.startsWith("/uploads/")
                || requestURI.startsWith("/css/")
                || requestURI.startsWith("/js/")
                || requestURI.startsWith("/images/")
                || requestURI.equals("/favicon.ico")
                || requestURI.endsWith(".png")
                || requestURI.endsWith(".jpg")
                || requestURI.endsWith(".jpeg")
                || requestURI.endsWith(".gif")
                || requestURI.endsWith(".webp")
                || requestURI.endsWith(".svg")
                || isAllowURLwithoutAuthentication(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }



        String token = request.getHeader("Authorization");

        if (isAllowURLwithoutAuthentication(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        else {
            String username = null;
            if (token != null) {
                try {
                    if (token.startsWith("Bearer "))
                        token = token.substring(7);

                    Pageable page = PageRequest.of(0, 1);
                    List<LoginHistory> history = loginHistoryRepository.findByAccessToken(token,
                            page);
                    if (history == null || history.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("JWT Token is expired.");
                        return;
                    }
                    username = jwtUtil.extractUsername(token);
                } catch (ExpiredJwtException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("JWT Token is expired.");
                    return;
                }
            }

            String path = request.getRequestURI();
            if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (username != null && !username.isBlank() && jwtUtil.validateToken(token, username)) {
                request.setAttribute("USER_NAME", username);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        }
    }

    private boolean isAllowURLwithoutAuthentication(String urlValue) {


        if (urlValue.contains("/sign-up") || urlValue.contains("/login")){
            return true;
        }else {
            return false;
        }
    }
}