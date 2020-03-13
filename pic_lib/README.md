# 简介
这里面主要是一些封装好的工具类，可能在各个模块中都会用到，所以就单独抽取出来了。这个是一个慢慢积累的过程，工具类越来越多，这个模块就越强大。

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


项目中涉及到图片加载，就想着写一个通用的接口，用来加载图片，其实加载图片接口应该很简单，
一个承载图片的载体，一般都是ImageView，
一个图片来源，可能是本地图片，网络图片，资源图片，bitmap等，
一个错误图片，如果加载失败显示的图片地址
一个角度，现在的图片一般都是圆角的，圆角的角度
一个上下文，例如Context，View，Activity等，这个可以为null
所以定义的接口，除了上下文和图片来源类型不确定，其他都是确定，那么定义的接口就如下
/**
 *
 * @param t 可以是 View，Context，Activity，Fragment
 * @param imageView   加载的ImageView
 * @param k  加载的资源，可以是路径，Bitmap,res资源等
 * @param defalutResId 默认的资源id
 * @param radius 图片圆角角度
 * @param <T>
 * @param <K>
 */
<T, K> void loadImge(T t, ImageView imageView, K k, @DrawableRes int defalutResId, int radius);

因为一般情况下，APP的错误加载图片和圆角是固定的，所以可以提前设置好，那么整个接口定义就如下
public interface ILoadImage {

    /**
     * 设置通用的默认角度和默认加载图片
     *
     * @param defalutRaius
     * @param defalutResId
     */
    void init(int defalutRaius, int defalutResId);

    /**
     *
     * @param t 可以是 View，Context，Activity，Fragment，可以理解为是一个占位符，如果其他框架不需要，可以设置为null
     * @param imageView   加载的ImageView
     * @param k  加载的资源，可以是路径，Bitmap,res资源等
     * @param defalutResId 默认的资源id
     * @param radius 图片圆角角度
     * @param <T>
     * @param <K>
     */
    <T, K> void loadImge(T t, ImageView imageView, K k, @DrawableRes int defalutResId, int radius);

    <T, K> void loadImge(T t, ImageView imageView, K k, @DrawableRes int defalutResId);

    <T, K> void loadImge(T t, ImageView imageView, K k);
}

然后使用glide进行实现相应的接口
public class GlideClient implements ILoadImage {

}
最后再通过一个工厂类，产生一个加载图片的客户端
public class ImageFactory {

    public static ILoadImage getClient() {
        return GlideClient.getInstance();
    }

}
这样使用的时候，只需要
ImageFactory.getClient().loadImge(itemView, imageView, path);
这样就可以了加载图片了，如果以后不想用的Glide的话，使用其他图片加载框架，Picasso框，那么定义一个 PicassonClient 实现 ILoadImage,然后在ImageFacctory 中产生PicassonClient即可。
