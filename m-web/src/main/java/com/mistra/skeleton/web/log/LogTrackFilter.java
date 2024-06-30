package com.mistra.skeleton.web.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.Marker;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description: 日志追踪id过滤器，每一次请求会分配唯一的requestId。配合logback.xml使用
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
public class LogTrackFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        int levelInt = level.toInt();
        String levelName = MDC.get("logLevel");
        if (StringUtils.isBlank(levelName) || "DEBUG,INFO,TRACE,WARN".indexOf(levelName) == -1) {
            return FilterReply.NEUTRAL;
        }
        if ((Level.TRACE.levelStr.equals(levelName) && levelInt >= Level.TRACE_INT)
                || (Level.DEBUG.levelStr.equals(levelName) && levelInt >= Level.DEBUG_INT)
                || (Level.INFO.levelStr.equals(levelName) && levelInt >= Level.INFO_INT)
                || (Level.WARN.levelStr.equals(levelName) && levelInt >= Level.WARN_INT)) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
