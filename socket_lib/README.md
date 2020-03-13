# 简介
我们知道，socket请求，最基础的就是连接socket，发送数据且得到结果，所以就可以把SocketClient的的接口定义好，
```java
public interface SocketClient {
    /**
     * tcp 连接
     *
     * @param host ip地址
     * @param port 端口号
     * @return
     */
    boolean connect(String host, int port);

    /**
     * 发送数据
     *
     * @param action tcp 发送的action
     * @param json   tcp 发送的数据
     * @return
     * @throws Exception 异常信息
     */
    String sendMessage(String action, String json) throws Exception;
}
```

不管使用哪种框架发送socket请求，实现这个接口即可，然后通过工厂模式，产生一个SocketClient,
```java
public class SocketFactory {

    public SocketClient getSocketClient() {
        return NettyClient.getInstance();
    }
}
```
因为这次使用的是netty框架，所以就直接定义了一个NettyClient，并且实现了SocketClient
```java
public class NettyClient implements SocketClient {
 @Override
    public boolean connect(String host, int port) {
        return connectSync(host, port);
    }

    @Override
    public String sendMessage(String action, String json) throws Exception {
        return send(action, json);
    }
}
```

这样在具体业务逻辑的时候，就可以使用了，定义了一个SocketManager,通过单例模式，
```java
private static SocketManager mSingleton = null;
private SocketClient mSocketClient;

public static SocketManager getInstance() {
    if (mSingleton == null) {
        synchronized (SocketManager.class) {
            if (mSingleton == null) {
                mSingleton = new SocketManager();
            }
        }
    }
    return mSingleton;
}

private SocketManager() {
    SocketFactory factory = new SocketFactory();
    mSocketClient = factory.getSocketClient();
}

```
这样使用的时候还是SocketClient对象，只不过实例化的时候，就变成了NettyClient了，这样，如果更换其他Socket框架，只需要定义新的SocketClient,然后在SocketFactory中进行返回即可。

# 使用步骤

1. 在项目的根目录build.gradle 文件中 的添加下面的maven库

```java
allprojects {
    repositories {
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
    implementation 'com.dcg.socket:socket_lib:0.0.1'
}
```
