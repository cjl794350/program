package com.edu.analysis.config;

import com.edu.analysis.common.Result;
import com.edu.analysis.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 登录拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(header);
        if (StringUtils.hasText(token) && token.startsWith(prefix)) {
            token = token.substring(prefix.length()).trim();
        }
        if (StringUtils.hasText(token) && jwtUtil.validate(token)) {
            return true;
        }
        // 未登录或令牌无效
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(401, "未登录或登录已过期");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
        return false;
    }
}
