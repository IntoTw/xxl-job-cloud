## xxl-job spring cloud version
use eureka instead of original http registry way.
## How to use
##### 1. mvn install the project new xxl-job-core jar for 'xxl-job-core 2.2.0.Cloud' version.
```shell
cd xxl-job-core;
mvn install;
```
##### 2. add new version jar to your executor projects.
```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.2.0.Cloud</version>
</dependency>
```
##### 3. add package scan to 'com.xxl.job.core.cloud' to enable configure.
```java
@EnableFeignClients(basePackages = "cn.intotw.rdcj.*.api")
@EnableEurekaClient
//like this
@SpringBootApplication(exclude = JacksonAutoConfiguration.class,
        scanBasePackages = {"cn.intotw.rdcj","com.xxl.job.core.cloud"})
@MapperScan("cn.intotw.rdcj.*.dao")
@EnableHystrix
public class RdcjUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(RdcjUserApplication.class);
    }
}
```
##### 4. and run xxl-job-admin from this project(do not fogot edit your eureka config).

##### 5. use it like original.