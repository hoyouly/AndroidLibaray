自己编新的版本

 gradle pic_lib:uploadArchives ; gradle util_lib:uploadArchives ;gradle top_lib:uploadArchives ;gradle uploadArchives

# 使用步骤

1. 在项目的根目录build.gradle 文件中 的添加下面的maven库

```java
allprojects {
    repositories {
          //top_lib使用到 constraint_layout 2.0.0-beta4 的maven库地址
          maven {
              url 'https://maven.google.com/'
              name 'Google'
          }
          //top_lib使用到 CustomPopwindow 的maven库地址
          maven {
              url 'https://jitpack.io'
          }

          // 自己搭建的 maven 的公有地址
          maven {
              url 'https://raw.github.com/hoyouly/maven-repository/master'
          }
    }
  }

```
2. 在app目录下面build.gradle文件中，就可以直接使用对应的组件库了，例如

```java
dependencies {
    implementation 'com.dcg.top:top_lib:0.0.1'
    implementation 'com.dcg.socket:socket_lib:0.0.1'
    implementation 'com.dcg.rfid:rfid_lib:0.0.1'
    implementation 'com.dcg.selectphoto:selectphoto_lib:0.0.1'
    implementation 'com.dcg.util:util_lib:0.0.1'
}
```
