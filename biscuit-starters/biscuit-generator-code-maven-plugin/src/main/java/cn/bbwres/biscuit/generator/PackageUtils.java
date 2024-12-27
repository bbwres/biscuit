package cn.bbwres.biscuit.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;

import java.io.File;

/**
 * 包工具
 * @author zhanglinfeng
 */
public class PackageUtils {


    /**
     * 获取包名
     * @param parent
     * @param subPackage
     * @return
     */
    public static  String joinPackage(String parent, String subPackage) {
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

    /**
     * 获取路径
     * @param parentDir
     * @param packageName
     * @return
     */
    public static  String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\." , StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }
}
