# 简介
定义了一个Fragment，SelectPhotoFragment 继承TopFragment,主要功能如下
1. 选择图片对话框，是从相册中选择还是调用摄像头拍照选择照片，
2. 动态申请权限
3. 封装了清除照片的功能clear()，
4. 得到所有照片的路径getLocalPicPathList(),
5. 设置图片的路径，setLocalPathList()等，让用户操作图片。

GrallyActiviy是继承TopActivity,里面可以预览大图，或者删除图片等。用户点击选择后的图片，
在SelectPhotoFragment 中是缩略图，然后点击进入GrallyActiviy界面才是原图。
# 使用

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
    implementation 'com.dcg.selectphoto:selectphoto_lib:0.0.1'
}
```

在其他页面如果需要选择照片功能，只需要添加以下代码即可,创建的Fragment的时候，就指定了最多可以选择多少张图片。定制化更高。
```java
private SelectPhotoFragment fragment;

fragment = SelectPhotoFragment.newInstance(BuildConfig.APPLICATION_ID,5);
getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .commitNowAllowingStateLoss();
    fragment.setClickItemStartActivityClass(PreviewActivity.class);
```

PreviewActivity就可以按照UI的样式定义，只需要加载GrallyFragmen即可，

```java
public class PreviewActivity extends TopMvpActivity<TopMvpPresenter> {

    private ArrayList<String> mPicList;
    private int currentIndex = 0;
    private GalleryFragment fragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview;
    }

    @Override
    protected int getTitleNameResId() {
        return 0;
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mPicList = intent.getStringArrayListExtra("pics");
            currentIndex = intent.getIntExtra("current_position", 0);
            if (mPicList.isEmpty()) {
                finish();
            }
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        mTitleBar.setLeftBack(v -> {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("pics", mPicList);
            setResult(RESULT_OK, intent);
            finish();
        });
        mTitleBar.setImageRes(TitleBar.FLAG_SEARCH, R.mipmap.icon_delete);
        mTitleBar.setSearchClick(v -> {
            mPicList.remove(currentIndex);
            fragment.setPicList(mPicList);
        });
    }

    @Override
    public void initView() {
        fragment = GalleryFragment.newInstance(mPicList, currentIndex);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNowAllowingStateLoss();
        fragment.setOnItemScrollChangeListener((view, position) -> {
            currentIndex = position;
            mTitleBar.setTitle((currentIndex + 1) + "/" + mPicList.size());
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当按下返回键时所执行的命令
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 此处写你按返回键之后要执行的事件的逻辑
            Intent intent = new Intent();
            intent.putStringArrayListExtra("pics", mPicList);
            setResult(RESULT_OK, intent);

            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected TopMvpPresenter setPresent() {
        return new TopMvpPresenter();
    }
}
```

这样就可以了，所有的关于选择图片，预览图片，删除照片，都在selectPhotoLibrary module中完成，用户只需要在特定的位置得到图片路径 fragment.getLocalPathList()，然后做相应的操作即可，例如上传等。
