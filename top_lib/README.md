# 简介
基础的架构和业务没有关系，只是把通用的部分封装起来，并且符合MVP架构模式。
主要包括一下几个类
* TopActivity ：最基础的Activity，里面把最基础的部分部分封装好。例如支持ButterKnife，设置透明状态栏，同时定义了通用的TitleBar,并且定义了一些常用的方法，例如showToast(),定义了一个Handler，这样子类就不需要在创建了，直接使用即可，还定义了一些其他必须要配置项，都是一些抽象的方法，例如定义标题，除了标题栏之外的布局文件等，
* TopFragment: 最基础的Fragment，功能和TopActivity类似，只不过一个是Fragment，一个是Activity，而已。
* TopMvpActivity: 符合MVP架构的Activity，继承TopActivity，实现了TopMvpView，在这个Activity中做一些符合MVP模式的业务。比如得到Presenter，把Presenter的生命周期和Activity进行绑定等。然后定义了新的通用方法，例如showLoding(),hideLoding()等，以及对一些error的公共处理，比如就是提示用户等。
* TopListActivity : 单列表Activity，继承TopMvpActivity，因为我们知道，很多页面其实就是一个列表页面，可能会加上一个head或者foot而已，所以可以把列表通用的部分封装到一块，包括了上拉刷新，下来加载，自定义头布局等。实现了BaseListMvpView 接口，从而更好的处理列表展示页面。泛型中需要传递该列表中item对应的实体bean和对应的Presenter的实现类。这样，具体到某个列表页面，直接继承TopListActivity即可，把不同的地方配置一下即可。
* TopMvpPresenter  最基础的Presenter，所有的业务Presenter都是它的子类，里面封装了对View的操作，根据View的操作，对attach的View进行管理，同时保留了一个Context对象，从而Presenter中有需要Context的地方，直接使用即可。
* TopMvpView : 最基础的View，定义了最常用的方法，例如showToash(),showLoding(),hideLoding(),onSucess(),onError()等。
* TopListMvpView: 继承BaseMvpView,又添加了新的方法，loadData() 加载数据，saveData() 保存数据。showEmptyView()是否显示空布局，appendData() 追加数据等
* TitleBar: 标题栏，包括左侧的返回，中间的标题，右侧的icon或者文案，用户只需要设置对应的标题，或者右侧的业务即可。
* TopApplication: 顶级的Application类，主要定义了主线程的Handler对象，还有就是可以得到Application的context

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
}
```
