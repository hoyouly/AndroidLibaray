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
    implementation 'com.dc.top:top_lib:0.0.2'
    implementation 'com.dc.socket:socket_lib:0.0.2'
    implementation 'com.dc.rfid:rfid_lib:0.0.2'
    implementation 'com.dc.selectphoto:selectphoto_lib:0.0.2'
    implementation 'com.dc.util:util_lib:0.0.2'
}
```
