package com.xxl.job.core.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by Chenxiang
 * @edit 静态中能获取到列表
 * @generator: IntelliJ IDEA
 * @description:
 * @project: xxl-job-cloud
 * @package: com.xxl.job.admin.core.cloud
 * @date: 2020年07月02日 14时01分
 */
@Component("springContext")
public class SpringContext {
    @Autowired
    DiscoveryClient discoveryClient;

    private static SpringContext springContext;
    @PostConstruct
    public void initialize() {
        springContext= this;
        springContext.discoveryClient=this.discoveryClient;
    }
    public static String getEurekaAddressList(String appName){
        //may be springContext not init
        if(springContext!=null){
            DiscoveryClient discoveryClient = SpringContext.springContext.discoveryClient;
            List<ServiceInstance> instances = discoveryClient.getInstances(appName);
            StringBuilder addressBuilder = new StringBuilder();
            for (int i = 0; i < instances.size(); i++) {
                addressBuilder.append(instances.get(i).getUri().toString());
                if(i!=instances.size()) {
                    addressBuilder.append(",");
                }
            }
            return addressBuilder.toString();
        }else {
            return "";
        }

    }
}
