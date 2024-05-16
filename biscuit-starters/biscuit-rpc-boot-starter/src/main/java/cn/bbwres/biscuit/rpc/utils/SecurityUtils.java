package cn.bbwres.biscuit.rpc.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 安全工具类
 *
 * @author zhanglinfeng
 */
public class SecurityUtils {


    /**
     * 执行数据的hash操作
     *
     * @param clientName        客户端名称
     * @param clientPassword    客户端密码
     * @param currentTimeMillis 当前时间
     * @return 返回hash数据
     */
    public static String hashDataInfo(String clientName, String clientPassword, String currentTimeMillis) {
        String msg = clientName + currentTimeMillis + clientPassword;
        return DigestUtils.md5DigestAsHex(msg.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 检查hash值是否符合
     *
     * @param clientName        客户端名称
     * @param clientPassword    客户端密码
     * @param currentTimeMillis 当前时间
     * @param hashInfo          hash 数据
     * @return true 匹配，false 不匹配
     */
    public static boolean checkDataInfo(String clientName, String clientPassword, String currentTimeMillis, String hashInfo) {
        String dataHash = hashDataInfo(clientName, clientPassword, currentTimeMillis);
        return dataHash.equals(hashInfo);

    }

}
