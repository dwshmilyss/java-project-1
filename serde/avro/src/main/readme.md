### 根据avsc文件生成对应的java类
```aidl
在pom.xml中添加avro插件

 <plugin>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-maven-plugin</artifactId>
    <version>1.8.2</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>schema</goal>
            </goals>
            <configuration>
                <sourceDirectory>
                    ${project.basedir}/src/main/resources/
                </sourceDirectory>
                <outputDirectory>
                    ${project.basedir}/src/main/java/
                </outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
其中：
${project.basedir}/src/main/resources/ 指定了avsc所在的目录
${project.basedir}/src/main/java/ 指定了生成的java类所在的目录       
```