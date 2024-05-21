package cn.bbwres.biscuit.rpc.filter;

import cn.bbwres.biscuit.rpc.utils.SecurityUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 负载均衡请求参数增强
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class RpcLoadBalancerRequestTransformer implements LoadBalancerRequestTransformer {


    /** {@inheritDoc} */
    @Override
    public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
        if (Objects.isNull(instance)) {
            return request;
        }
        Map<String, List<String>> stringListMap = SecurityUtils.putHeaderAuthorizationInfo(instance);
        if (!CollectionUtils.isEmpty(stringListMap)) {
            for (String headerName : stringListMap.keySet()) {
                request.getHeaders().put(headerName, stringListMap.get(headerName));
            }
        }
        return request;
    }
}
