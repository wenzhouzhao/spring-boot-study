# spring-boot-actuator

> Spring Boot + actuator，实现可检查项目运行情况。

将项目运行起来之后，会在**控制台**里查看所有可以访问的端口信息
1. 打开浏览器，访问：http://localhost:8090/sys/actuator/mappings ，输入用户名(codecloud)密码(123456)即可看到所有的 mapping 信息
2. 访问：http://localhost:8090/sys/actuator/beans ，输入用户名(codecloud)密码(123456)即可看到所有 Spring 管理的 Bean
3. 其余可访问的路径，参见文档
  - actuator 文档：https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready
  - 具体可以访问哪些路径：https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready-endpoints


