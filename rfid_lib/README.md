# 简介
应该所有的物联网项目都会涉及到RFID扫描，得到RFID数据的功能，所以可以把这一块单独抽取成一个module，如果RFID功能API有变化，也只修改这一块，对外提供的API接口是不变的。
RFID 功能模块分两种
1.单次RFID扫描功能
2.批量RFID扫描功能
所以定义一个RFIDManager统一管理RFID的功能，使用单例模式，
主要包括一下接口方法
registerSingleRfid()注册单次扫描RFID功能
unregitserSingleRfid() 取消单次扫描RFID功能
startBatchRfidScan() 发送广播，开启批量RFID扫描功能
stopBatchRfidScann() 发送停止批量RFID扫描功能
registerBatchRfid()  注册批量RFID功能
unregisterBatchRfid()取消批量RFID功能
因为需要得到RIFD返回的数据，所以就定义了一个接口，通过监听回调方式处理

```java
/**
 * 定义一个接口
 */
public interface RfidListener {

    void onRfidListener(String epc);
}

/**
 * 定义一个变量储存数据
 */
private RfidListener rfidListener;

/**
 * 提供公共的方法,并且初始化接口类型的数据
 */
public void setOnRfidListener(RfidListener listener) {
    this.rfidListener = listener;
}
```
这样在AbsListWithSearchActivity和AbsMvpActivity中实现这个接口，
然后定义了接口，是否需要这样的功能即可
```java
//AbsListWithSearchActivity
@Override
protected void onStart() {
    super.onStart();
    rfidScanManager = RFIDScanManager.getInstance(this);
    if (isRegistSingleRFID()) {
        rfidScanManager.registerSingleRfid();
    } else if (isRegistBatchRFID()) {
        rfidScanManager.registerBatchRfid();
    }
    rfidScanManager.setOnRfidListener(this);
}

/**
 * 是否需要注册单次扫描RFID，默认false，不注册
 *
 * @return
 */
public boolean isRegistSingleRFID() {
    return false;
}

/**
 * 接收RFID扫描的结果
 *
 * @param epc
 */
@Override
public void onRfidListener(String epc) {
    synchronized (AbsListWithSearchActivity.this) {
        getRfidResult(epc);
    }
}

public void getRfidResult(String epc) {

}

/**
 * 是否需要注册批量扫描RFID，默认false，不注册
 *
 * @return
 */
public boolean isRegistBatchRFID() {
    return false;
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (isRegistSingleRFID()) {
        rfidScanManager.unregitserSingleRfid();
    } else if (isRegistBatchRFID()) {
        rfidScanManager.stopBatchRfidScann();
    }
}
```
那么整个app项目中都有这样的功能，如果需要RFID功能，直接设置成true，然后重写getRfidResult()即可。


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
    implementation 'com.dcg.rfid:rfid_lib:0.0.1'
}
```
