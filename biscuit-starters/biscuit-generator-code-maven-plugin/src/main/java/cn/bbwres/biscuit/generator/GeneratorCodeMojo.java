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

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Properties;

/**
 * 代码生成mojo
 *
 * @author zhanglinfeng
 */
@Mojo(name = "generatorCode", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorCodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;


    /**
     * 执行命令
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("read-config:" + ((Resource) project.getResources().getFirst()).getDirectory());

        ReadConfig readConfig = new ReadConfig(((Resource) project.getResources().getFirst()).getDirectory());
        Properties projectConfig = readConfig.readProjectConfig(getLog());
        String outputDir = System.getProperty("user.dir");
        String author = projectConfig.getProperty("author");
        if (StringUtils.isBlank(author)) {
            author = readConfig.scannerNext("author:", null);
        }
        String parentPath = projectConfig.getProperty("parentPath");
        if (StringUtils.isBlank(parentPath)) {
            parentPath = readConfig.scannerNext("parentPath:", null);
        }
        String tableNames = projectConfig.getProperty("tableNames");
        if (StringUtils.isBlank(tableNames)) {
            tableNames = readConfig.scannerNext("Please enter the table names, separated by English commas:", null);
        }
        String outputDirConfig = projectConfig.getProperty("outputDir");
        if (StringUtils.isBlank(outputDirConfig)) {
            outputDir = readConfig.scannerNext("outputDir? default:" + outputDir, outputDir);
        } else {
            outputDir = outputDirConfig;
        }
        String dbUrl = projectConfig.getProperty("dbUrl");
        if (StringUtils.isBlank(dbUrl)) {
            dbUrl = readConfig.scannerNext("dbUrl:", null);
        }
        String dbUsername = projectConfig.getProperty("dbUsername");
        if (StringUtils.isBlank(dbUsername)) {
            dbUsername = readConfig.scannerNext("dbUsername:", null);
        }
        String dbPassword = projectConfig.getProperty("dbPassword");
        if (StringUtils.isBlank(dbPassword)) {
            dbPassword = readConfig.scannerNext("dbPassword:", null);
        }

        String useTenant = projectConfig.getProperty("useTenant");
        if (StringUtils.isBlank(useTenant)) {
            useTenant = "false";
        }
        //orm的框架
        String ormName = projectConfig.getProperty("ormName");
        if (StringUtils.isBlank(ormName)) {
            ormName = "mybatis-plus";
        }
        //是否覆盖文件
        String enableFileOverride = projectConfig.getProperty("enableFileOverride");
        if (StringUtils.isBlank(enableFileOverride)) {
            enableFileOverride = "false";
        }

        DataSourceConfig.Builder builder = new DataSourceConfig.Builder(dbUrl, dbUsername, dbPassword);
        Generator generator = new Generator(builder, outputDir, readConfig.readBiscuitConfig(ormName,getLog()),
                author, parentPath, tableNames, useTenant, enableFileOverride);
        generator.generator();
    }

}
