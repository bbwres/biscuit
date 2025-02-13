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

package cn.bbwres.biscuit.generator;

import cn.bbwres.biscuit.utils.JsonUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 生成代码
 *
 * @author zhanglinfeng
 */
public class Generator {


    private final DataSourceConfig.Builder dbConfigBuilder;
    private final String outputDir;

    private final Properties prop;
    private final String author;
    private final String parentPath;
    private final String tableNames;
    /**
     * 是否使用租户
     */
    private final String useTenant;

    public Generator(DataSourceConfig.Builder dbConfigBuilder, String outputDir,
                     Properties prop, String author, String parentPath, String tableNames, String useTenant) {
        this.dbConfigBuilder = dbConfigBuilder;
        this.outputDir = outputDir;
        this.prop = prop;
        this.author = author;
        this.parentPath = parentPath;
        this.tableNames = tableNames;
        this.useTenant = useTenant;
    }

    /**
     * 生成文件
     */
    public void generator() {
        FastAutoGenerator generator = FastAutoGenerator.create(dbConfigBuilder);
        // 全局配置
        generator.globalConfig(builder -> {
            builder.author(author).outputDir(outputDir + "/src/main/java");
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.GLOBAL_CONFIG_ENABLE_SWAGGER))) {
                builder.enableSwagger();
            }
            builder.disableOpenDir();
        });

        // 配置策略
        generatorStrategyConfig(generator)
                // 包配置
                .packageConfig(builder -> builder.parent(parentPath)
                        .mapper(prop.getProperty(GeneratorConstant.PACKAGE_CONFIG_MAPPER))
                        .service(prop.getProperty(GeneratorConstant.PACKAGE_CONFIG_SERVICE))
                        .serviceImpl(prop.getProperty(GeneratorConstant.PACKAGE_CONFIG_SERVICE_IMPL))
                        .xml(prop.getProperty(GeneratorConstant.PACKAGE_CONFIG_XML))
                        .pathInfo(Collections.singletonMap(OutputFile.xml,
                                outputDir + "/src/main/resources/mapper")));
        //配置注入策略
        generatorInjectionConfig(generator)
                .execute();
    }

    /**
     * 代码生成时，注入策略配置
     *
     * @param generator
     * @return
     */
    public FastAutoGenerator generatorInjectionConfig(FastAutoGenerator generator) {
        // 策略配置
        return generator.injectionConfig(builder -> {
            Map<String, Object> customMap = new HashMap<>(8);

            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.INJECTION_CONFIG_BASE_FIELDS))) {
                List<String> baseFields = getBaseFields(prop.getProperty(GeneratorConstant.INJECTION_CONFIG_BASE_FIELDS));
                customMap.put("baseDOFields", baseFields);
            }

            customMap.put("convertPackage", PackageUtils.joinPackage(parentPath, "convert"));
            customMap.put("vo", PackageUtils.joinPackage(parentPath, "vo"));
            builder.customMap(customMap);
            builder.beforeOutputFile((tableInfo, stringObjectMap) -> {
                if (StringUtils.isNotBlank(tableInfo.getComment())) {
                    tableInfo.setComment(tableInfo.getComment().replaceAll("\\s*|\r|\n|\t", ""));
                }
                stringObjectMap.put("idKey", tableInfo.getFields().stream()
                        .filter(TableField::isKeyFlag).findFirst().get());
                Set<String> importPackages = new HashSet<>();
                for (TableField field : tableInfo.getFields()) {
                    if (null != field.getColumnType() && null != field.getColumnType().getPkg()) {
                        importPackages.add(field.getColumnType().getPkg());
                    }
                    if (StringUtils.isNotBlank(field.getComment())) {
                        field.setComment(field.getComment().replaceAll("[\r\n\t]", ","));
                    }
                }
                stringObjectMap.put("importPackages", importPackages);
            });
            customFiles(prop.getProperty(GeneratorConstant.INJECTION_CONFIG_CUSTOM_FILES), builder);

        });
    }

    /**
     * 代码生成策略配置
     *
     * @param generator
     * @return
     */
    public FastAutoGenerator generatorStrategyConfig(FastAutoGenerator generator) {
        return generator.strategyConfig(builder -> {
            builder.addInclude(getTables(tableNames));
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_TABLE_PREFIX))) {
                builder.addTablePrefix(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_TABLE_PREFIX));
            }
            Entity.Builder eb = builder.entityBuilder();
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_ENABLE_LOMBOK))) {
                eb.enableLombok();
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_ID_TYPE))) {
                eb.idType(IdType.valueOf(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_ID_TYPE)));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_FORMAT_FILE_NAME))) {
                eb.formatFileName(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_FORMAT_FILE_NAME));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_JAVA_TEMPLATE))) {
                eb.javaTemplate(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_JAVA_TEMPLATE));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_NAMING))) {
                eb.naming(NamingStrategy.valueOf(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_NAMING)));
            }
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_ENABLE_CHAIN_MODEL))) {
                eb.enableChainModel();
            }
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_ENABLE_TABLE_FIELD_ANNOTATION))) {
                eb.enableTableFieldAnnotation();
            }
            if (Boolean.parseBoolean(useTenant)) {
                if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_SUPER_TENANT_CLASS))) {
                    eb.superClass(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_SUPER_TENANT_CLASS));
                }
            } else {
                if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_SUPER_CLASS))) {
                    eb.superClass(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_ENTITY_SUPER_CLASS));
                }
            }
            Service.Builder sb = eb.serviceBuilder();
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_FORMAT_SERVICE_FILE_NAME))) {
                sb.formatServiceFileName(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_FORMAT_SERVICE_FILE_NAME));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_FORMAT_SERVICE_IMPL_FILE_NAME))) {
                sb.formatServiceImplFileName(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_FORMAT_SERVICE_IMPL_FILE_NAME));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_SERVICE_TEMPLATE))) {
                sb.serviceTemplate(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_SERVICE_TEMPLATE));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_SERVICE_IMPL_TEMPLATE))) {
                sb.serviceImplTemplate(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_SERVICE_SERVICE_IMPL_TEMPLATE));
            }

            Controller.Builder cb = sb.controllerBuilder();
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_CONTROLLER_FORMAT_FILE_NAME))) {
                cb.formatFileName(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_CONTROLLER_FORMAT_FILE_NAME));
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_CONTROLLER_TEMPLATE))) {
                cb.template(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_CONTROLLER_TEMPLATE));
            }

            Mapper.Builder mb = cb.mapperBuilder();
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_MAPPER_ANNOTATION))) {
                try {
                    mb.mapperAnnotation((Class<? extends Annotation>) Class.forName(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_MAPPER_ANNOTATION)));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_SUPER_CLASS))) {
                mb.superClass(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_SUPER_CLASS));
            }
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_ENABLE_BASE_RESULT_MAP))) {
                mb.enableBaseResultMap();
            }
            if (Boolean.parseBoolean(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_ENABLE_BASE_COLUMN_LIST))) {
                mb.enableBaseColumnList();
            }
            if (StringUtils.isNotBlank(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_MAPPER_TEMPLATE))) {
                mb.mapperTemplate(prop.getProperty(GeneratorConstant.STRATEGY_CONFIG_MAPPER_MAPPER_TEMPLATE));
            }
            mb.build();
        });
    }

    /**
     * 获取表名
     *
     * @param tables 表名字符串
     * @return 表名列表
     */
    protected List<String> getTables(String tables) {
        return Arrays.asList(tables.split(","));
    }

    /**
     * 配置自定义文件
     *
     * @param customFile
     */
    protected void customFiles(String customFile, InjectionConfig.Builder builder) {
        if (StringUtils.isBlank(customFile)) {
            return;
        }
        List<Map<String, String>> customFileConfig = JsonUtil.toObject(customFile, new TypeReference<>() {
        });
        if (CollectionUtils.isEmpty(customFileConfig)) {
            return;
        }
        List<CustomFile> customFiles = new ArrayList<>(16);

        for (Map<String, String> stringObjectMap : customFileConfig) {
            customFiles.add(new CustomFile
                    .Builder()
                    .templatePath(stringObjectMap.get("templatePath"))
                    .packageName(stringObjectMap.get("packageName"))
                    .fileName(stringObjectMap.get("fileName"))
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName().replace("Entity", ""))
                    .build());
        }
        builder.customFile(customFiles);

    }

    /**
     * 获取基础字段
     *
     * @param baseFields
     * @return
     */
    protected List<String> getBaseFields(String baseFields) {
        return Arrays.asList(baseFields.split(","));
    }
}
