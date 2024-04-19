package cn.bbwres.biscuit.gateway.router;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * 路由配置信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/router")
public class RouterController {

    private final RouteLocator routeLocator;


    @Value("${spring.application.name}")
    private String appName;


    /**
     * <p>appNames.</p>
     *
     * @return a {@link java.util.Set} object
     */
    @GetMapping(value = "/appNames")
    public Set<String> appNames() {
        Set<String> appNames = new HashSet<>(8);
        // 获取网关中配置的路由
        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                .filter(route -> !appName.equals(route.getUri().getHost()))
                .subscribe(route -> appNames.add(route.getUri().getHost()));

        return appNames;
    }

}
