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

package cn.bbwres.biscuit.gateway.route;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.gateway.route.entity.GatewayRouteInfoEntity;
import cn.bbwres.biscuit.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 网关路由默认配置
 *
 * @author zhanglinfeng
 */
@Slf4j
public class DefaultGatewayRoute implements GatewayRoute, ApplicationEventPublisherAware {

    public static final String EMPTY_JSON_RULE_MULTIPLE = "[]";
    private final RouteDefinitionLocator routeDefinitionLocator;

    private final RouteDefinitionWriter routeDefinitionWriter;

    private final GatewayProperties gatewayProperties;

    private ApplicationEventPublisher applicationEventPublisher;

    public DefaultGatewayRoute(RouteDefinitionLocator routeDefinitionLocator, RouteDefinitionWriter routeDefinitionWriter,
                               GatewayProperties gatewayProperties) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 增加路由信息
     *
     * @param gatewayRouteInfoEntity
     */
    @Override
    public synchronized void add(GatewayRouteInfoEntity gatewayRouteInfoEntity) {
        if (gatewayRouteInfoEntity == null) {
            throw new SystemRuntimeException("Gateway dynamic route is null");
        }

        Map<String, RouteDefinition> routeDefinitionMap = locateRoutes();
        String routeId = gatewayRouteInfoEntity.getId();
        if (routeDefinitionMap.containsKey(routeId)) {
            throw new SystemRuntimeException("Gateway dynamic route for routeId=[" + routeId + "] is duplicated");
        }
        RouteDefinition routeDefinition = convertRoute(gatewayRouteInfoEntity);
        addRoute(routeDefinition);
        log.info("Added Gateway dynamic route={}", routeDefinition);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));

    }

    /**
     * 修改路由信息
     *
     * @param gatewayRouteInfoEntity
     */
    @Override
    public synchronized void modify(GatewayRouteInfoEntity gatewayRouteInfoEntity) {
        if (gatewayRouteInfoEntity == null) {
            throw new SystemRuntimeException("Gateway dynamic route is null");
        }

        Map<String, RouteDefinition> routeDefinitionMap = locateRoutes();
        String routeId = gatewayRouteInfoEntity.getId();

        if (!routeDefinitionMap.containsKey(routeId)) {
            throw new SystemRuntimeException("Gateway dynamic route for routeId=[" + routeId + "] isn't found");
        }

        RouteDefinition routeDefinition = convertRoute(gatewayRouteInfoEntity);
        modifyRoute(routeDefinition);

        log.info("Modified Gateway dynamic route={}", routeDefinition);

        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 删除路由信息
     *
     * @param routeId
     */
    @Override
    public synchronized void delete(String routeId) {
        if (StringUtils.isEmpty(routeId)) {
            throw new SystemRuntimeException("RouteId is empty");
        }

        Map<String, RouteDefinition> routeDefinitionMap = locateRoutes();
        RouteDefinition routeDefinition = routeDefinitionMap.get(routeId);
        if (routeDefinition == null) {
            throw new SystemRuntimeException("Gateway dynamic route for routeId=[" + routeId + "] isn't found");
        }

        deleteRoute(routeDefinition);

        log.info("Deleted Gateway dynamic route for routeId={}", routeId);

        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }


    /**
     * 更新数据
     *
     * @param gatewayRouteEntityList
     */
    @Override
    public void updateAll(List<GatewayRouteInfoEntity> gatewayRouteEntityList) {
        if (CollectionUtils.isEmpty(gatewayRouteEntityList)) {
            throw new SystemRuntimeException("Gateway dynamic routes are null");
        }

        boolean isIdDuplicated = isIdDuplicated(gatewayRouteEntityList);
        if (isIdDuplicated) {
            throw new SystemRuntimeException("Gateway dynamic routes have duplicated routeIds");
        }

        Map<String, RouteDefinition> dynamicRouteDefinitionMap = gatewayRouteEntityList.stream().collect(Collectors.toMap(GatewayRouteInfoEntity::getId, this::convertRoute));
        Map<String, RouteDefinition> currentRouteDefinitionMap = locateRoutes();

        List<RouteDefinition> addRouteDefinitionList = new ArrayList<RouteDefinition>(dynamicRouteDefinitionMap.size());
        List<RouteDefinition> modifyRouteDefinitionList = new ArrayList<RouteDefinition>(dynamicRouteDefinitionMap.size());
        List<RouteDefinition> deleteRouteDefinitionList = new ArrayList<RouteDefinition>(dynamicRouteDefinitionMap.size());

        for (Map.Entry<String, RouteDefinition> entry : dynamicRouteDefinitionMap.entrySet()) {
            String routeId = entry.getKey();
            RouteDefinition routeDefinition = entry.getValue();
            if (!currentRouteDefinitionMap.containsKey(routeId)) {
                addRouteDefinitionList.add(routeDefinition);
            }
        }

        for (Map.Entry<String, RouteDefinition> entry : dynamicRouteDefinitionMap.entrySet()) {
            String routeId = entry.getKey();
            RouteDefinition routeDefinition = entry.getValue();
            if (currentRouteDefinitionMap.containsKey(routeId)) {
                RouteDefinition currentRouteDefinition = currentRouteDefinitionMap.get(routeId);
                if (!currentRouteDefinition.equals(routeDefinition)) {
                    modifyRouteDefinitionList.add(routeDefinition);
                }
            }
        }

        for (Map.Entry<String, RouteDefinition> entry : currentRouteDefinitionMap.entrySet()) {
            String routeId = entry.getKey();
            RouteDefinition routeDefinition = entry.getValue();
            if (!dynamicRouteDefinitionMap.containsKey(routeId)) {
                deleteRouteDefinitionList.add(routeDefinition);
            }
        }

        for (RouteDefinition routeDefinition : addRouteDefinitionList) {
            addRoute(routeDefinition);
        }

        for (RouteDefinition routeDefinition : modifyRouteDefinitionList) {
            modifyRoute(routeDefinition);
        }

        for (RouteDefinition routeDefinition : deleteRouteDefinitionList) {
            deleteRoute(routeDefinition);
        }

        log.info("--- Gateway Dynamic Routes Update Information ----");
        log.info("Total count={}", gatewayRouteEntityList.size());
        log.info("Added count={}", addRouteDefinitionList.size());
        log.info("Modified count={}", modifyRouteDefinitionList.size());
        log.info("Deleted count={}", deleteRouteDefinitionList.size());
        log.info("--------------------------------------------------");

        if (addRouteDefinitionList.isEmpty() && modifyRouteDefinitionList.isEmpty() && deleteRouteDefinitionList.isEmpty()) {
            return;
        }

        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));

    }

    /**
     * 更新数据
     *
     * @param gatewayStrategyRouteConfig
     */
    @Override
    public void updateAll(String gatewayStrategyRouteConfig) {
        if (StringUtils.isBlank(gatewayStrategyRouteConfig)) {
            gatewayStrategyRouteConfig = EMPTY_JSON_RULE_MULTIPLE;
        }
        List<GatewayRouteInfoEntity> gatewayStrategyRouteEntityList = JsonUtil.toObject(gatewayStrategyRouteConfig, new TypeReference<>() {
        });
        updateAll(gatewayStrategyRouteEntityList);
    }


    /**
     * 获取已经生效的路由信息
     *
     * @return
     */
    public Map<String, RouteDefinition> locateRoutes() {

        Flux<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions();
        try {
            //TODO 待测试
            List<RouteDefinition> routeDefinitionList = routeDefinitions.collectList().block();
            if (CollectionUtils.isEmpty(routeDefinitionList)) {
                return new HashMap<>(2);
            }

            return routeDefinitionList.stream().collect(Collectors.toMap(RouteDefinition::getId, routeDefinition -> routeDefinition));
        } catch (Exception e) {
            return new HashMap<>(2);
        }
    }

    /**
     * 检查id是否已经存在
     *
     * @param gatewayRouteInfoEntityList
     * @return
     */
    private boolean isIdDuplicated(List<GatewayRouteInfoEntity> gatewayRouteInfoEntityList) {
        Set<GatewayRouteInfoEntity> gatewayStrategyRouteEntitySet = new TreeSet<>(Comparator.comparing(GatewayRouteInfoEntity::getId));
        gatewayStrategyRouteEntitySet.addAll(gatewayRouteInfoEntityList);
        return gatewayStrategyRouteEntitySet.size() < gatewayRouteInfoEntityList.size();
    }

    /**
     * 转换路由配置
     *
     * @param gatewayRouteInfoEntity
     * @return
     */
    public RouteDefinition convertRoute(GatewayRouteInfoEntity gatewayRouteInfoEntity) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRouteInfoEntity.getId());
        routeDefinition.setUri(convertURI(gatewayRouteInfoEntity.getUri()));

        List<String> predicateList = gatewayRouteInfoEntity.getPredicates();
        List<GatewayRouteInfoEntity.Predicate> userPredicates = gatewayRouteInfoEntity.getUserPredicates();
        List<PredicateDefinition> predicateDefinitionList = new ArrayList<>(predicateList.size() + userPredicates.size());
        for (String predicate : predicateList) {
            predicateDefinitionList.add(new PredicateDefinition(predicate));
        }
        for (GatewayRouteInfoEntity.Predicate predicate : userPredicates) {
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            predicateDefinition.setName(predicate.getName());
            predicateDefinition.setArgs(predicate.getArgs());
            predicateDefinitionList.add(predicateDefinition);
        }
        routeDefinition.setPredicates(predicateDefinitionList);

        List<String> filterList = gatewayRouteInfoEntity.getFilters();
        List<GatewayRouteInfoEntity.Filter> userFilters = gatewayRouteInfoEntity.getUserFilters();
        List<FilterDefinition> filterDefinitionList = new ArrayList<FilterDefinition>(filterList.size() + userFilters.size());
        for (String filter : filterList) {
            filterDefinitionList.add(new FilterDefinition(filter));
        }
        for (GatewayRouteInfoEntity.Filter filter : userFilters) {
            FilterDefinition filterDefinition = new FilterDefinition();
            filterDefinition.setName(filter.getName());
            filterDefinition.setArgs(filter.getArgs());
            filterDefinitionList.add(filterDefinition);
        }
        routeDefinition.setFilters(filterDefinitionList);

        routeDefinition.setOrder(gatewayRouteInfoEntity.getOrder());
        routeDefinition.setMetadata(gatewayRouteInfoEntity.getMetadata());


        return routeDefinition;
    }

    /**
     * 路由定义转换为配置
     *
     * @param routeDefinition
     * @return
     */
    public GatewayRouteInfoEntity convertRoute(RouteDefinition routeDefinition) {
        GatewayRouteInfoEntity gatewayStrategyRouteEntity = new GatewayRouteInfoEntity();
        gatewayStrategyRouteEntity.setId(routeDefinition.getId());
        gatewayStrategyRouteEntity.setUri(routeDefinition.getUri().toString());
        gatewayStrategyRouteEntity.setOrder(routeDefinition.getOrder());
        try {
            gatewayStrategyRouteEntity.setMetadata(routeDefinition.getMetadata());
        } catch (Throwable e) {

        }
        convertPredicates(routeDefinition.getPredicates(), gatewayStrategyRouteEntity.getPredicates(), gatewayStrategyRouteEntity.getUserPredicates());
        convertFilters(routeDefinition.getFilters(), gatewayStrategyRouteEntity.getFilters(), gatewayStrategyRouteEntity.getUserFilters());

        return gatewayStrategyRouteEntity;
    }

    /**
     * 转换断言信息
     *
     * @param predicateDefinitionList
     * @param predicateList
     * @param userPredicateList
     */
    public void convertPredicates(List<PredicateDefinition> predicateDefinitionList, List<String> predicateList, List<GatewayRouteInfoEntity.Predicate> userPredicateList) {
        for (PredicateDefinition predicateDefinition : predicateDefinitionList) {
            String name = predicateDefinition.getName();
            Map<String, String> args = predicateDefinition.getArgs();
            boolean internal = isInternal(args);
            if (internal) {
                predicateList.add(String.format("%s=%s", name, StringUtils.join(args.values(), ",")));
            } else {
                GatewayRouteInfoEntity.Predicate predicate = new GatewayRouteInfoEntity.Predicate();
                predicate.setName(predicateDefinition.getName());
                predicate.setArgs(predicateDefinition.getArgs());

                userPredicateList.add(predicate);
            }
        }
    }

    /**
     * 转换过滤器信息
     *
     * @param filterDefinitionList
     * @param filterList
     * @param userFilterList
     */
    public void convertFilters(List<FilterDefinition> filterDefinitionList, List<String> filterList, List<GatewayRouteInfoEntity.Filter> userFilterList) {
        for (FilterDefinition filterDefinition : filterDefinitionList) {
            String name = filterDefinition.getName();
            Map<String, String> args = filterDefinition.getArgs();
            boolean internal = isInternal(args);
            if (internal) {
                filterList.add(String.format("%s=%s", name, StringUtils.join(args.values(), ",")));
            } else {
                GatewayRouteInfoEntity.Filter filter = new GatewayRouteInfoEntity.Filter();
                filter.setName(filterDefinition.getName());
                filter.setArgs(filterDefinition.getArgs());
                userFilterList.add(filter);
            }
        }
    }

    public boolean isInternal(Map<String, String> args) {
        for (Map.Entry<String, String> entry : args.entrySet()) {
            String key = entry.getKey();
            // 如果key包含_genkey_，表示为网关内置配置，例如，Path，RewritePath的key都会以_genkey_来命名
            if (key.contains("_genkey_")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 转换url信息
     *
     * @param value
     * @return
     */
    public URI convertURI(String value) {
        URI uri;
        if (value.toLowerCase().startsWith("http") || value.toLowerCase().startsWith("https")) {
            uri = UriComponentsBuilder.fromHttpUrl(value).build().toUri();
        } else {
            uri = URI.create(value);
        }

        return uri;
    }


    /**
     * 新增路由配置
     *
     * @param routeDefinition
     */
    public void addRoute(RouteDefinition routeDefinition) {
        Disposable disposable = null;
        try {
            disposable = routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        } finally {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }

    /**
     * 修改路由配置
     *
     * @param routeDefinition
     */
    public void modifyRoute(RouteDefinition routeDefinition) {
        deleteRoute(routeDefinition);
        addRoute(routeDefinition);
    }

    /**
     * 删除路由配置
     *
     * @param routeDefinition
     */
    public void deleteRoute(RouteDefinition routeDefinition) {
        Disposable disposable = null;
        try {
            disposable = routeDefinitionWriter
                    .delete(Mono.just(routeDefinition.getId()))
                    .onErrorResume(throwable -> {
                        if (throwable instanceof NotFoundException) {
                            gatewayProperties.getRoutes().removeIf(routeCandidate -> routeCandidate.getId().equals(routeDefinition.getId()));

                            return Mono.empty();
                        }

                        return Mono.error(throwable);
                    }).subscribe();
        } finally {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }

    /**
     * Set the ApplicationEventPublisher that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * Invoked before ApplicationContextAware's setApplicationContext.
     *
     * @param applicationEventPublisher event publisher to be used by this object
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
