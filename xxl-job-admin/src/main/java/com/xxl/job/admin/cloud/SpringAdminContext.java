package com.xxl.job.admin.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by Chenxiang
 * @edit
 * @generator: IntelliJ IDEA
 * @description:
 * @project: xxl-job-cloud
 * @package: com.xxl.job.admin.core.cloud
 * @date: 2020年07月02日 14时01分
 */
@Component("springAdminContext")
public class SpringAdminContext {
    @Autowired
    DiscoveryClient discoveryClient;

    private static SpringAdminContext springAdminContext;
    @PostConstruct
    public void initialize() {
        springAdminContext = this;
        springAdminContext.discoveryClient=this.discoveryClient;
    }
    public static String getEurekaAddressList(String appName){
        //may be springContext not init
        if(springAdminContext !=null){
            DiscoveryClient discoveryClient = SpringAdminContext.springAdminContext.discoveryClient;
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
