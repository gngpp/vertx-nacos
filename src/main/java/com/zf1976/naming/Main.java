package com.zf1976.naming;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.zf1976.naming.util.InetUtils;
import com.zf1976.naming.util.InetUtilsProperties;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @date 2021/5/13
 */
public class Main {

    public static void main(String[] args) throws NacosException {

        Vertx vertx = Vertx.vertx();
        NamingService naming = NamingFactory.createNamingService("localhost:8848");
        Instance instance = new Instance();
        InetUtils.HostInfo host = new InetUtils(new InetUtilsProperties())
                .findFirstNonLoopbackHostInfo();
        // 注册实例ip
        instance.setIp(host.getIpAddress());
        // 注册实例端口
        instance.setPort(8313);
        Map<String, String> instanceMeta = new HashMap<>();
        instance.setMetadata(instanceMeta);
        // 监控健康
        instance.setHealthy(true);

        // 注册服务名
        naming.registerInstance("vertx-service", instance);
        Router router = Router.router(vertx);
        router.get("/hello")
              .handler( ctx -> {
                  ctx.response().end("{\"who\": \"im vertx\"}");
              });
        vertx.createHttpServer()
             .requestHandler(router)
             .listen(8313);

        List<Route> routes = router.getRoutes();
    }
}
