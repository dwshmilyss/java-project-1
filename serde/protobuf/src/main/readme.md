### 生成对应的PB java类
```aidl
1. cd /Users/edz/sourceCode/java-project-1/serde/protobuf/src/main/resources
2. protoc UserActionLog.proto --java_out=/Users/edz/sourceCode/java-project-1/serde/protobuf/src/main/java
```
##### .proto文件如下
```aidl
syntax="proto3";

package com.yiban.serde.protobuf.model;

option java_package = "com.yiban.serde.protobuf.model";
option java_multiple_files=true;
option java_outer_classname="UserActionLogModel";

message UserActionLog
{
  string userName = 1;
  string actionType = 2;
  string ipAddress = 3;
  int32 gender = 4;
  string provience = 5;
}

其中：
1. package用以区分不同包下的类(如果不指定option java_package 则使用此配置)
2. option java_package 指定了类所在的包路径，注意这个值会被设置到类的代码中(即 package xxx.xx.xx)
3. option java_multiple_files=true 会生成3个类文件(每个类对应一个java文件)，如果不配置该项，则只会生成一个类文件(里面包含了三个java类)
```