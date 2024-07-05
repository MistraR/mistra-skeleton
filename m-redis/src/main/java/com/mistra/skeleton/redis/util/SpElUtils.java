package com.mistra.skeleton.redis.util;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author rui.wang
 * @date 2023/7/5
 * @ Description: spring el表达式解析
 */
public class SpElUtils {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String parseSpEl(Method method, Object[] args, String spEl) {
        // 解析参数名
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});
        // el解析需要的上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        Expression expression = parser.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }

    public static String getMethodKey(Method method) {
//        return method.getDeclaringClass() + "#" + method.getName();
        return method.getName();
    }
}
