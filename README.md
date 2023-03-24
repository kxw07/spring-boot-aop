# spring-boot-aop

### Run with IDE
1. install Ajc compile plugin
2. add java agent to VM options
   -javaagent:pathto/spring-instrument-{version}.jar
   -javaagent:pathto/aspectjweaver-{version}.jar

### Run with jar 
java -jar -javaagent:pathto/spring-instrument-{version}.jar \
-javaagent:pathto/aspectjweaver-{version}.jar \
./target/spring-boot-aop-0.0.1-SNAPSHOT.jar

### Problems
1. Use Ajc compile cause Lombok error. 