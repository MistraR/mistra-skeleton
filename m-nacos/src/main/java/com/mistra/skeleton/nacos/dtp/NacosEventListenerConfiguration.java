package com.mistra.skeleton.nacos.dtp;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.mistra.skeleton.web.dtp.DTPContext;
import com.mistra.skeleton.web.dtp.DTPProperties;
import com.mistra.skeleton.web.dtp.EnableDiversityThreadPool;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/16 17:28
 * @ Description: 通过Nacos实现动态线程池
 */
@Slf4j
@Configuration
@ConditionalOnClass(value = NacosConfigManager.class)
public class NacosEventListenerConfiguration {

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private Environment environment;

    @Resource
    private DTPProperties DTPProperties;

    @EventListener
    public void listener(ApplicationStartedEvent event) throws NacosException {
        EnableDiversityThreadPool annotation = AnnotationUtils.getAnnotation(event.getSpringApplication().getMainApplicationClass(),
                EnableDiversityThreadPool.class);
        if (annotation != null) {
            init();
        }
    }

    public void init() throws NacosException {
        Listener listener = new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                val properties = doParse(configInfo);
                bindDTPProperties(properties, DTPProperties);
                log.info("Nacos config refresh DTPProperties:{}", DTPProperties.toString());
                doRefresh(DTPProperties);
            }
        };
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length < 1) {
            profiles = environment.getDefaultProfiles();
        }

        String appName = environment.getProperty("spring.application.name");
        appName = StringUtils.isNoneBlank(appName) ? appName : "application";

        String fileType = environment.getProperty("spring.cloud.nacos.config.file-extension");
        fileType = StringUtils.isNoneBlank(fileType) ? fileType : "yml";

        String group = environment.getProperty("spring.cloud.nacos.config.group");
        group = StringUtils.isNoneBlank(group) ? group : DEFAULT_GROUP;

        String dataId = appName + "-" + profiles[0] + "." + fileType;
        nacosConfigManager.getConfigService().addListener(dataId, group, listener);
        log.info("Nacos dtp config eventListener resistry success");
    }

    private void doRefresh(DTPProperties DTPProperties) {
        DTPContext.IO_POOL.fixPoolSize(DTPProperties.getCorePoolSizeIO(), DTPProperties.getMaximumPoolSizeIO());
        DTPContext.CPU_POOL.fixPoolSize(DTPProperties.getCorePoolSizeCPU(), DTPProperties.getMaximumPoolSizeCPU());
        DTPContext.PRIORITY_POOL.fixPoolSize(DTPProperties.getCorePoolSizePriority(), DTPProperties.getMaximumPoolSizePriority());
        DTPContext.FUTURE_POOL.fixPoolSize(DTPProperties.getCorePoolSizeFuture(), DTPProperties.getMaximumPoolSizePriority());
        DTPContext.IO_POOL.autoExpansion.set(DTPProperties.isAutoExpansion());
        DTPContext.CPU_POOL.autoExpansion.set(DTPProperties.isAutoExpansion());
        DTPContext.PRIORITY_POOL.autoExpansion.set(DTPProperties.isAutoExpansion());
    }

    private Map<Object, Object> doParse(String configInfo) {
        if (StringUtils.isEmpty(configInfo)) {
            return Collections.emptyMap();
        }
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ByteArrayResource(configInfo.getBytes()));
        return bean.getObject();
    }

    public static void bindDTPProperties(Map<?, Object> properties, DTPProperties DTPProperties) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(sources);
        ResolvableType type = ResolvableType.forClass(DTPProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(DTPProperties);
        binder.bind("spring.dynamic.tp", target);
    }
}