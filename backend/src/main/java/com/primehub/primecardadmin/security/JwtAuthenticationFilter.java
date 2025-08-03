package com.primehub.primecardadmin.security;

import com.primehub.primecardadmin.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 排除不需要JWT认证的路径
        String requestPath = request.getRequestURI();
        logger.debug("JWT过滤器处理请求路径: " + requestPath);
        if (shouldSkipFilter(requestPath)) {
            logger.debug("跳过JWT认证，路径: " + requestPath);
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token格式为 "Bearer token"，移除Bearer前缀获取token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("JWT Token解析失败", e);
            }
        } else {
            logger.debug("JWT Token不是以Bearer开头或不存在");
        }

        // 验证token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // 如果token有效，则配置Spring Security使用该token
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置当前用户为已认证
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断是否应该跳过JWT过滤器
     */
    private boolean shouldSkipFilter(String requestPath) {
        // 排除认证相关路径，但不包括/api/auth/me
        if (requestPath.startsWith("/api/auth/") && !requestPath.equals("/api/auth/me")) {
            return true;
        }
        
        // 排除H2控制台
        if (requestPath.startsWith("/api/h2-console")) {
            return true;
        }
        
        // 排除OpenAPI文档相关路径
        if (requestPath.startsWith("/api/v3/api-docs") ||
            requestPath.startsWith("/api/swagger-ui") ||
            requestPath.equals("/api/swagger-ui.html") ||
            requestPath.startsWith("/api/swagger-resources") ||
            requestPath.startsWith("/api/webjars")) {
            return true;
        }
        
        return false;
    }
}