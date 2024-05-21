package cn.bbwres.biscuit.rpc.utils;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全工具类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
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


    /**
     * 请求头中放入认证参数
     *
     * @param instance 服务实例
     * @return a {@link java.util.Map} object
     */
    public static Map<String, List<String>> putHeaderAuthorizationInfo(ServiceInstance instance) {
        Map<String, List<String>> headers = new HashMap<>(16);
        Map<String, String> metadata = instance.getMetadata();
        String clientName = instance.getServiceId();
        String clientPassword = metadata.get(RpcConstants.CLIENT_PASSWORD);
        //加密算法的实现
        String currentTimeMillis = System.currentTimeMillis() + "";
        String authorization = SecurityUtils.hashDataInfo(clientName, clientPassword, currentTimeMillis);
        headers.put(RpcConstants.AUTHORIZATION_HEADER_NAME, List.of(authorization));
        headers.put(RpcConstants.CLIENT_TIME_HEADER_NAME, List.of(currentTimeMillis));
        return headers;
    }

}
