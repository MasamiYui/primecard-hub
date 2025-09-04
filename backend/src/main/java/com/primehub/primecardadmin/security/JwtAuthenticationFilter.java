package com.primehub.primecardadmin.security;

import com.primehub.primecardadmin.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 排除不需要JWT认证的路径
        String requestPath = request.getRequestURI();
        String clientIp = getClientIpAddress(request);
        logger.debug("JWT过滤器处理请求 - 路径: {}, IP: {}", requestPath, clientIp);
        
        if (shouldSkipFilter(requestPath)) {
            logger.debug("跳过JWT认证 - 路径: {}", requestPath);
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
                logger.debug("JWT Token解析成功 - 用户: {}", username);
            } catch (Exception e) {
                logger.warn("JWT Token解析失败 - IP: {}, Token前缀: {}", clientIp, 
                           jwtToken.length() > 10 ? jwtToken.substring(0, 10) + "..." : jwtToken, e);
            }
        } else {
            logger.debug("JWT Token格式无效或不存在 - IP: {}, Authorization头: {}", 
                        clientIp, requestTokenHeader != null ? "存在但格式错误" : "不存在");
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
                logger.info("用户认证成功 - 用户: {}, IP: {}, 路径: {}", username, clientIp, requestPath);
            } else {
                logger.warn("JWT Token验证失败 - 用户: {}, IP: {}", username, clientIp);
            }
        } else if (username != null) {
            logger.debug("用户已认证，跳过重复认证 - 用户: {}", username);
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
        
        // 排除C端新闻资讯接口
        if (requestPath.startsWith("/api/client/news")) {
            return true;
        }
        
        return false;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}