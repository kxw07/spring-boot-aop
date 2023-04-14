# spring-boot-aop

AspectJ 設定資訊
===
###### tags: `AOP`

reference:
https://www.baeldung.com/aspectj
https://www.eclipse.org/aspectj/
https://juejin.cn/post/6844903760347529224

Spring AOP 使用簡單，但功能較少，透過 proxy 方式做到，像 @Transactional，所以當切面沒有 source code 就無法做到，例如你想切在最底層 RestTemplate 上，而不是用同一個 RestTemplate 去注入。

AspectJ 使用起來較複雜，但基本上所有地方都切得到，官方文件的 weave 可以解讀為把 class 做加工，類似 Lombok。

Weaving time 分成三種：
1. Compile-Time Weaving
2. Post-Compile Weaving (aka binary weaving)
3. Load-Time Weaving


## Compile-Time Weaving
使用場景為切在 source code，透過 AspectJ compile(Ajc) 產出加工後的 class。
有測試成功。

IntelliJ 如果同時使用 Ajc compile 和 Lombok 要調整以下。
1. Go to "Compiler" and check "Delegate to javac".
2. Go to "Project settings" and check "Post-compile weave mode".


### Aspect class
```java
@Aspect
public class CustomRestTemplateAspect {
    @Pointcut(value = "execution(* com.example.springbootaop.rest.CustomRestTemplate.exchange(..))")
    public void exchange() {
    }

    @Around("exchange()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CustomRestTemplateAspect before");
        Object response = joinPoint.proceed();
        System.out.println("CustomRestTemplateAspect after");

        return response;
    }
}
```

### pom.xml
如果沒使用 Lombok 可用以下 plugin 進行 maven package
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8 </encoding>
    </configuration>
    <executions>
        <execution>
            <goals>
                <!-- use this goal to weave all your main classes -->
                <goal>compile</goal>
                <!-- use this goal to weave all your test classes -->
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

如果要同時使用 Lombok 和 AspectJ 則 maven package 要用以下設定，
Lombok 先產生 class，Ajc 再對這些 Class 做 weaving，等於於 Post-Compile Weaving 了。
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8</encoding>
        <excludes>
            <exclude>**/*.java</exclude>
        </excludes>
        <forceAjcCompile>true</forceAjcCompile>
    </configuration>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>process-classes</phase>
            <goals>
                <!-- use this goal to weave all your main classes -->
                <goal>compile</goal>
            </goals>
            <configuration>
                <weaveDirectories>
                    <weaveDirectory>${project.build.outputDirectory}</weaveDirectory>
                </weaveDirectories>
            </configuration>
        </execution>
        <execution>
            <id>default-testCompile</id>
            <phase>process-test-classes</phase>
            <goals>
                <!-- use this goal to weave all your test classes -->
                <goal>test-compile</goal>
            </goals>
            <configuration>
                <weaveDirectories>
                    <weaveDirectory>${project.build.testOutputDirectory}</weaveDirectory>
                </weaveDirectories>
            </configuration>
        </execution>
    </executions>
</plugin>
```


## Post-Compile Weaving (aka binary weaving)
使用場景為切在沒有 source code 的地方，例如第三方套件、class、jar。
有在 maven package 測試成功。

### Aspect class
```java
@Aspect
public class PostCompileAspect {
    @Pointcut(value = "execution(* com.squareup.okhttp.OkHttpClient.newCall(..))")
    public void execute() {
    }

    @Around("execute()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("execute before");
        Object response = joinPoint.proceed();
        System.out.println("execute after");

        return response;
    }
}
```

### pom.xml
最主要的是這段，指定你想要 weaving 的目標。
```xml
<weaveDependencies>
    <weaveDependency>
        <groupId>com.squareup.okhttp</groupId>
        <artifactId>okhttp</artifactId>
    </weaveDependency>
</weaveDependencies>
```

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8</encoding>
        <weaveDependencies>
            <weaveDependency>
                <groupId>com.squareup.okhttp</groupId>
                <artifactId>okhttp</artifactId>
            </weaveDependency>
        </weaveDependencies>
    </configuration>
    <executions>
        <execution>
            <goals>
                <!-- use this goal to weave all your main classes -->
                <goal>compile</goal>
                <!-- use this goal to weave all your test classes -->
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```


## Load-Time Weaving
任何場景都可以用，但設定複雜，概念為透過 AspectJ 的 javaagent 在載入 class 時做加工。
有測試成功。

### Aspect class
```java
@Aspect
public class RestTemplateAspect {
    @Pointcut(value = "execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public void exchange() {
    }

    @Around("exchange()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("RestTemplateAspect before");
        Object response = joinPoint.proceed();
        System.out.println("RestTemplateAspect after");

        return response;
    }
}
```

### META-INF/aop.xml
```xml
<aspectj>
    <aspects>
        <aspect name="com.example.springbootaop.config.RestTemplateAspect"/>
    </aspects>

    <weaver options="-verbose -showWeaveInfo">
        <include within="org.springframework.web.client.RestTemplate"/>
    </weaver>
</aspectj>
```

### pom.xml
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8</encoding>
    </configuration>
    <executions>
        <execution>
            <goals>
                <!-- use this goal to weave all your main classes -->
                <goal>compile</goal>
                <!-- use this goal to weave all your test classes -->
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 執行時要指定 java agent
```shell
-javaagent:pathto/spring-instrument-{version}.jar
-javaagent:pathto/aspectjweaver-{version}.jar 
```

成功時，在啟動會看到類似以下的訊息
```
[LaunchedURLClassLoader@7bfcd12c] info AspectJ Weaver Version 1.9.7 built on Thursday Jun 24, 2021 at 16:14:45 PDT
[LaunchedURLClassLoader@7bfcd12c] info register classloader org.springframework.boot.loader.LaunchedURLClassLoader@7bfcd12c
[LaunchedURLClassLoader@7bfcd12c] info using configuration file:/Users/kai/dojo/java/spring-boot-aop/target/spring-boot-aop-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/META-INF/aop.xml
[LaunchedURLClassLoader@7bfcd12c] info register aspect com.example.springbootaop.config.RestTemplateAspect
[LaunchedURLClassLoader@7bfcd12c] warning javax.* types are not being woven because the weaver option '-Xset:weaveJavaxPackages=true' has not been specified
```

## 可相容 Lombok 的 maven package 設定
讓 Lombok 先 build，Ajc 只針對前者的結果 class 加工。
可用在 Comile-Time Weaving 和 Load-Time Weaving。
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8</encoding>
        <excludes>
            <exclude>**/*.java</exclude>
        </excludes>
        <forceAjcCompile>true</forceAjcCompile>
    </configuration>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>process-classes</phase>
            <goals>
                <!-- use this goal to weave all your main classes -->
                <goal>compile</goal>
            </goals>
            <configuration>
                <weaveDirectories>
                    <weaveDirectory>${project.build.outputDirectory}</weaveDirectory>
                </weaveDirectories>
            </configuration>
        </execution>
        <execution>
            <id>default-testCompile</id>
            <phase>process-test-classes</phase>
            <goals>
                <!-- use this goal to weave all your test classes -->
                <goal>test-compile</goal>
            </goals>
            <configuration>
                <weaveDirectories>
                    <weaveDirectory>${project.build.testOutputDirectory}</weaveDirectory>
                </weaveDirectories>
            </configuration>
        </execution>
    </executions>
</plugin>
```

