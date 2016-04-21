# visual-framework
### this is a highly customizable framework
#### with container , cache, event bus, distributed session, mvc, rest service, and everything in ui page you can monitor
<br/>
#### configuration is open service
<br />
### here is the architecture of visual-framework:
![visual-framework-art](https://github.com/wenlaizhou/visual-framework/raw/master/core/src/main/java/cn/framework/core/pool/visual-framework.png)
<br/>
### and you can design your system as you think , for example:
![framework-service-art](https://github.com/wenlaizhou/visual-framework/raw/master/core/src/main/java/cn/framework/core/pool/framework-service.png)
<br />
## user guid
### 1 code and play with visual-framework:
create maven project and edit pom:

add repository
```xml
<repository>
    <id>wenlai-repo</id>
    <name>framework</name>
    <url>http://visual-framework.com:8088/nexus/content/repositories/framework/</url>
</repository>
```
add dependencies
<br/>
framework.version = 2.0.2
```xml
<dependency>
    <groupId>cn.framework</groupId>
    <artifactId>core</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>cn.framework</groupId>
    <artifactId>cache</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>cn.framework</groupId>
    <artifactId>db</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>cn.framework</groupId>
    <artifactId>mvc</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>cn.framework</groupId>
    <artifactId>rest</artifactId>
    <version>${framework.version}</version>
</dependency>
```
add application boot point
```java
public class Start {

    public static void main(String[] args) throws Exception {
        FrameworkStart.START(args);
    }
}
```
deploy on server and run