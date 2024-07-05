package com.mistra.skeleton.feign.web;

import java.io.IOException;
import org.springframework.core.annotation.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/4/22
 */
@Slf4j
@Order(-1)
@jakarta.servlet.annotation.WebFilter(filterName = "webFilter", urlPatterns = "/datablox-node/open/*")
public class MistraWebFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            log.info("uri:{} 包装RequestWrapper", ((HttpServletRequest) servletRequest).getRequestURI());
            //重新包装HttpServletRequest为RequestWrapper，以提供重复读写request body的能力
            ServletRequest request = new RequestWrapper((HttpServletRequest) servletRequest);
            chain.doFilter(request, servletResponse);
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

}
