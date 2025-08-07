/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.nacos.operation;

import com.alibaba.nacos.api.config.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

/**
 * nacos 配置操作类
 *
 * @author zhanglinfeng
 */

public abstract class AbstractNacosProcessor implements DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNacosProcessor.class);
    private final ExecutorService executorService;

    private final NacosConfigOperation nacosConfigOperation;

    public AbstractNacosProcessor(NacosConfigOperation nacosConfigOperation) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(4);
        executor.setCorePoolSize(2);
        executor.setQueueCapacity(4);
        executor.setThreadNamePrefix("nacos-config");
        executor.setKeepAliveSeconds(0);
        executor.initialize();
        this.executorService = executor.getThreadPoolExecutor();
        this.nacosConfigOperation = nacosConfigOperation;
    }

    private Listener listener;

    @PostConstruct
    public void initialize() {
        beforeInitialization();
        String group = getGroup();
        String dataId = getDataId();

        try {
            listener = nacosConfigOperation.subscribeConfig(group, dataId, executorService, config -> {
                try {
                    callbackConfig(config);
                } catch (Exception e) {
                    LOG.warn("当前订阅group:[{}],dataId:[{}]处理异常", group, dataId, e);
                }
            });
        } catch (Exception e) {
            LOG.warn("当前订阅group:[{}],dataId:[{}]处理异常", group, dataId, e);
        }


        try {
            String config = nacosConfigOperation.getConfig(group, dataId);
            if (config != null) {
                callbackConfig(config);
            } else {
                LOG.warn("当前获取group:[{}],dataId:[{}]配置为空", group, dataId);
            }
        } catch (Exception e) {
            LOG.warn("当前获取group:[{}],dataId:[{}]配置异常", group, dataId, e);
        }

        afterInitialization();
    }

    @Override
    public void destroy() {
        if (listener == null) {
            return;
        }

        String group = getGroup();
        String dataId = getDataId();


        try {
            nacosConfigOperation.unsubscribeConfig(group, dataId, listener);
        } catch (Exception e) {
            LOG.warn("当前获取group:[{}],dataId:[{}]解除订阅异常", group, dataId, e);
        }

        executorService.shutdownNow();
    }


    /**
     * 获取Group
     *
     * @return
     */
    public abstract String getGroup();

    /**
     * 获取DataId
     *
     * @return
     */
    public abstract String getDataId();


    /**
     * 处理配置回调
     *
     * @param config
     */
    public abstract void callbackConfig(String config);


    /**
     * 前置处理
     */
    public void beforeInitialization() {

    }

    /**
     * 后置处理
     */
    public void afterInitialization() {

    }
}
