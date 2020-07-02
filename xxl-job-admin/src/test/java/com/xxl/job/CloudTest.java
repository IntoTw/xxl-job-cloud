package com.xxl.job;

import com.xxl.job.admin.XxlJobAdminApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Chenxiang
 *
 * @generator: IntelliJ IDEA
 * @description:
 * @project: xxl-job-cloud
 * @package: com.xxl.job
 * @date: 2020年07月02日 10时57分
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes={XxlJobAdminApplication.class,CloudTest.class})
public class CloudTest {
    @Autowired
    DiscoveryClient discoveryClient;
    @Test
    public void test(){
        List<String> services = discoveryClient.getServices();
        discoveryClient.getInstances("rdcj-user").get(0).getUri();
        System.out.println(1);
    }
}
