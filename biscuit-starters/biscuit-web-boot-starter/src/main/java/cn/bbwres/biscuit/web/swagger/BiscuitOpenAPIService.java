///*
// *
// *  * Copyright 2024 bbwres
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *      http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package cn.bbwres.biscuit.web.swagger;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springdoc.core.OpenAPIService;
//import org.springdoc.core.PropertyResolverUtils;
//import org.springdoc.core.SecurityService;
//import org.springdoc.core.SpringDocConfigProperties;
//import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
//import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
//import org.springdoc.core.providers.JavadocProvider;
//
//import java.lang.annotation.Annotation;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * OpenAPIService
// * 为了解决springdoc无法加载oauth2的端点问题
// *
// * @author zhanglinfeng
// */
//
//public class BiscuitOpenAPIService extends OpenAPIService {
//
//    private static final Logger LOG = LoggerFactory.getLogger(BiscuitOpenAPIService.class);
//
//    private final SpringDocConfigProperties springDocConfigProperties;
//
//    /**
//     * Instantiates a new Open api builder.
//     *
//     * @param openAPI                   the open api
//     * @param securityParser            the security parser
//     * @param springDocConfigProperties the spring doc config properties
//     * @param propertyResolverUtils     the property resolver utils
//     * @param openApiBuilderCustomizers the open api builder customisers
//     * @param serverBaseUrlCustomizers  the server base url customizers
//     * @param javadocProvider           the javadoc provider
//     */
//    public BiscuitOpenAPIService(Optional<OpenAPI> openAPI, SecurityService securityParser, SpringDocConfigProperties springDocConfigProperties,
//                                 PropertyResolverUtils propertyResolverUtils, Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
//                                 Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers, Optional<JavadocProvider> javadocProvider) {
//        super(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
//        this.springDocConfigProperties = springDocConfigProperties;
//    }
//
//    @Override
//    public OpenAPI build(Locale locale) {
//        OpenAPI build = super.build(locale);
//        Map<String, Object> mappingsMap = getMappingsMap();
//        if (springDocConfigProperties.isShowOauth2Endpoints()) {
//            try {
//                Class<? extends Annotation> frameworkEndpointAnnotation = (Class<? extends Annotation>) Class.forName("org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint");
//                mappingsMap.putAll(getContext().getBeansWithAnnotation(frameworkEndpointAnnotation));
//            } catch (Exception e) {
//                LOG.warn("当前springdoc开启了ShowOauth2Endpoints,加载异常", e);
//            }
//
//        }
//        return build;
//    }
//}
