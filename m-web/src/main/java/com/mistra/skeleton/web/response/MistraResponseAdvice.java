package com.mistra.skeleton.web.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/10/19 14:08
 * @ Description:
 */
@ControllerAdvice(annotations = RestController.class)
public class MistraResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return (returnType.hasMethodAnnotation(PostMapping.class) || returnType.hasMethodAnnotation(GetMapping.class)
                || returnType.hasMethodAnnotation(DeleteMapping.class) || returnType.hasMethodAnnotation(MistraResponse.class))
                && !returnType.hasMethodAnnotation(IgnoreResponseAdvice.class);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (selectedConverterType == StringHttpMessageConverter.class) {
            return objectMapper.writeValueAsString(ResponseResult.buildSuccess(body));
        }
        return ResponseResult.buildSuccess(body);
    }
}
