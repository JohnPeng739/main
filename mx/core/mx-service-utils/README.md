# mx-service-utils模块
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

## 安装和引用
*Maven*
```xml
<dependency>
    <groupId>org.mx</groupId>
    <artifactId>mx-service-utils</artifactId>
    <version>1.7.0</version>
</dependency>
```
*Gradle*
```gradle
compile 'org.mx:mx-service-utils:1.7.0'
```

## Hello World
- 第一步：引入配置<br>
创建一个测试配置类`com.dscomm.test.TestConfig`，如下所示：
```java
package com.dscomm.test;

@Configuration
@Import({SpringConfig.class})
public class TestConfig {}
```
`org.mx.service.server.config.ServerConfig`将自动载入CLASSPATH中的`server.properties`配置文件，并根据配置启动相应的RESTful、Servlet、Websocket服务器。
*注意*：如果自己基于Spring实现了相应的`@Service`、`@Component`，需要自己在Config类中进行扫描或者定义。
- 第二步：载入配置并启动应用<br>
创建一个Application类`com.dscomm.test.TestApplication`，如下所示：
```java
package com.dscomm.test;

public class TestApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(TestConfig.class);
    }
}
```
至此，你需要的服务器就以微服务的方式自动运行了。
*友情提醒*：鉴于Java应用运行特点，所有微服务最适合运行的环境是Linux中的进程环境，可以将每个微服务配置成Linux下的后台Daemon进程进行自动运行；如果在Windows环境中，则要防止Java运行进程（往往是一个终端进程）被用户误关闭的风险！！

## RESTful server
基于Jetty提供的HTTP服务器来实现RESTful服务。
### Server
要运行自定义的RESTful服务器，你需要修改CLASSPATH下的`server.properties`配置文件，如下所示：
```properties
restful.enabled=true
restful.port=9999
restful.security=false
restful.service.classes=restfulClassesTest
```
提醒：`restful.service.classes`可以同时配置多个服务集，名称之间用半角逗号分割。
同时，你还需要在你的应用Config文件中（如前例中的TestConfig），将你自己实现的RESTful Resource注入到Spring IoC容器，如下所示：
```java
public class TestConfig {
    @Bean("restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(Resource1.class, Resource2.class);
    }
}
```
*注意*：在Config中定义的RESTful Classes的name必须与server.properties配置文件中配置的名称一致。
### client
本模块还提供了面向RESTful服务的java客户端程序`org.mx.service.client.rest.RestClientInvoke`，便于后端Java应用调用别的RESTful服务。
具体使用方法参见Javadoc。

## Servlet server
基于Jetty提供的Servlet服务器来实现自定义的Servlet服务。
要运行自定义的Servlet服务器，你需要修改CLASSPATH下的`server.properties`配置文件，如下所示：
```properties
servlet.enabled=true
servlet.port=9998
servlet.security=false
servlet.service.classes=servletClassesTest
```
提醒：`servlet.service.classes`可以同时配置多个服务集，名称之间用半角逗号分割。
同时，你还需要在你的应用Config文件中（如前例中的TestConfig），Servlet注入到Spring IoC容器，如下所示：
```java
public class TestConfig {
    @Bean("servletClassesTest")
    public List<Class<?>> servletClassesTest() {
        return Arrays.asList(Servlet1.class, Servlet2.class);
    }
}
```
*注意*：
1. 在Config中定义的Servlet Classes的name必须与server.properties配置文件中配置的名称一致。
2. 所有注入的Servlet服务必须继承`BaseHttpServlet`。

## Websocket server
基于Jetty提供的Websocket服务器来实现自定义的Websocket服务。
### Server
要运行自定义的Websocket服务器，你需要修改CLASSPATH下的`server.properties`配置文件，如下所示：
```properties
websocket.enabled=true
websocket.port=9997
websocket.session.ping.cycleSec=5
websocket.session.clean.cycleSec=30
websocket.service.classes=websocketClassesTest
```
提醒：`websocket.service.classes`可以同时配置多个服务集，名称之间用半角逗号分割。
同时，你还需要在你的应用Config文件中（如前例中的TestConfig），将你自己实现的Websocket注入到Spring IoC容器，如下所示：
```java
public class TestConfig {
    @Bean("websocketClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(Websocket1.class, Websocket2.class);
    }
}
```
*注意*：
1. 在Config中定义的Websocket Classes的name必须与server.properties配置文件中配置的名称一致。
2. 所有注入的Websocket服务必须继承`DefaultWsSessionMonitor`。
### client
本模块还提供了面向Websocket服务的java客户端程序`org.mx.service.client.websocket.WsClientInvoke`，便于后端Java应用调用Websocket服务。
具体使用方法参见Javadoc。

## TCP/UDP server
提供的tcp/udp通信服务器。
### Server

### Client

*详细说明参阅本模块的Javadoc。*