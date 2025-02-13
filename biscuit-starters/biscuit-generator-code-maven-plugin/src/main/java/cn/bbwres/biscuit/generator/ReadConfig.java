package cn.bbwres.biscuit.generator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * 读取配置
 *
 * @author zhanglinfeng
 */
public class ReadConfig {

    private final String baseDir;

    public ReadConfig(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 读取基础配置
     *
     * @return 配置信息
     */
    public Properties readBiscuitConfig(Log log) {
        return readProperties("generator/biscuit.properties", log);
    }

    /**
     * 读取项目配置信息
     *
     * @return 配置信息
     */
    public Properties readProjectConfig(Log log) {
        return readProperties("generator.properties", log);
    }


    /**
     * 读取配置信息
     *
     * @param fileName 文件名称
     * @return 配置信息
     */
    public Properties readProperties(String fileName, Log log) {
        Properties prop = new Properties();
        String filePathname = baseDir + File.separator + fileName;
        log.info("读取配置文件路径为:" + filePathname);
        try (InputStream input = new FileInputStream(filePathname)) {
            //加载properties文件
            prop.load(input);
        } catch (IOException ex) {
            try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                log.info("项目目录不存在插件配置，开始读取插件默认配置" );
                prop.load(input);
                log.info("读取插件默认配置完成" );
            } catch (Exception e) {
                log.info("读取配置信息异常:" + e.getMessage());
            }
        }
        return prop;
    }

    /**
     * 读取控制台输入内容
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * 控制台输入内容读取并打印提示信息
     *
     * @param message    提示信息
     * @param defaultMsg 为空时的默认参数
     * @return String
     */
    public String scannerNext(String message, String defaultMsg) {
        System.out.println(message);
        String nextLine = scanner.nextLine();
        if (StringUtils.isBlank(nextLine)) {
            if (StringUtils.isNotBlank(defaultMsg)) {
                return defaultMsg;
            }
            // 如果输入空行继续等待
            return scanner.next();
        }
        return nextLine;
    }

}
