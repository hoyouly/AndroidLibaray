package com.dcg.selectphoto;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;
import android.widget.Toast;
import com.zhouyou.recyclerview.XRecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * @ Time  :  2020-02-17
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class SelectPhotoFragment extends Fragment {

    XRecyclerView xrv_review;
    private LinearLayoutManager layoutManager;
    private BaseAdapter mAdapter;
    private ArrayList<String> mLocalPicPathList;
    private static final String FRIST_PIC = "frist_pic";
    //文件最大值
    private static final int MAX_POST_SIZE = 10 * 1024 * 1024;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_PIC_REQUEST = 0xa2;
    private Dialog mSelectImageDialog;
    private File mPicFile;
    private Uri imageUri;
    private String mAuthority;
    //照片选择的最大数量
    private int mMaxCount = 5;

    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;

    private Class cls;


    public static SelectPhotoFragment newInstance(String authority, int maxCount) {
        Bundle bundle = new Bundle();
        bundle.putString("authority", authority);
        bundle.putInt("maxCount", maxCount);
        SelectPhotoFragment fragment = new SelectPhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_photo, container, false);
        initData();
        initView(view);
        return view;
    }


    public void initData() {
        mAuthority = getArguments().getString("authority") + ".fileprovider";
        mMaxCount = getArguments().getInt("maxCount");
        if (mLocalPicPathList == null) {
            mLocalPicPathList = new ArrayList<>();
            mLocalPicPathList.add(FRIST_PIC);
        }
    }

    public ArrayList<String> getLocalPathList() {
        return mLocalPicPathList;
    }

    /**
     * 设置点击item跳转的Activity
     *
     * @param cls
     */
    public void setClickItemStartActivityClass(Class cls) {
        this.cls = cls;
    }

    public void setLocalPicPathList(ArrayList<String> picLocalPathList) {
        this.mLocalPicPathList = picLocalPathList;
        mAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mLocalPicPathList.clear();
        mLocalPicPathList.add(FRIST_PIC);
        mAdapter.notifyDataSetChanged();
    }

    public void initView(View rootView) {
        xrv_review = rootView.findViewById(R.id.xrv_review);
        setDividerLine();
        xrv_review.setPullRefreshEnabled(false);
        xrv_review.setLoadingMoreEnabled(false);
        layoutManager = new GridLayoutManager(getContext(), 3);
        xrv_review.setLayoutManager(layoutManager);
        mAdapter = new BaseAdapter<String>(getContext(), R.layout.edit_pic_item, mLocalPicPathList) {
            @Override
            protected void convert(CommonViewHolder holder, String bean, int index) {
                if (FRIST_PIC.equals(bean)) {
                    holder.setImageResource(R.id.iv_pic, R.mipmap.icon_add_pic);
                    holder.itemView.setOnClickListener(v -> {
                        if (mMaxCount != -1 && mLocalPicPathList.size() == mMaxCount + 1) {
                            showToast(formatString(R.string.more_five_pic, mMaxCount));
                            return;
                        }
                        //弹出对话框
                        showSelectPicDialog();
                    });
                } else {
                    holder.setLocalImage(R.id.iv_pic, bean);
                    holder.itemView.setOnClickListener(v -> {
                            if (cls != null) {
                                Intent intent = new Intent(getContext(), cls);
                                ArrayList<String> subList = new ArrayList<>();
                                for (int i = 1; i < mLocalPicPathList.size(); i++) {
                                    subList.add(mLocalPicPathList.get(i));
                                }
                                intent.putStringArrayListExtra("pics", subList);
                                intent.putExtra("current_position", index - 1);
                                startActivityForResult(intent, CODE_PIC_REQUEST);
                            }
                        }
                    );
                }
            }
        };
        xrv_review.setAdapter(mAdapter);
    }

    /**
     * 设置分割线
     */
    protected void setDividerLine() {
        xrv_review.addItemDecoration(new GridLayoutItemDecoration(getContext(), R.drawable.item_divider_02));
    }

    private void showSelectPicDialog() {
        if (mSelectImageDialog == null) {
            mSelectImageDialog = new Dialog(getContext(), R.style.noTitleDialog);
            mSelectImageDialog.setContentView(R.layout.dialog_select_imge);
            Window window = mSelectImageDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.y = 20;
            window.setAttributes(params);
            mSelectImageDialog.findViewById(R.id.select_photo).setOnClickListener(v -> {
                autoObtainStoragePermission();
                dismissSelectDialog(mSelectImageDialog);
            });
            mSelectImageDialog.findViewById(R.id.take_camera).setOnClickListener(v -> {
                autoObtainCameraPermission();
                dismissSelectDialog(mSelectImageDialog);

            });
        }
        mSelectImageDialog.show();
    }

    private void dismissSelectDialog(Dialog selectImageDialog) {
        if (selectImageDialog != null) {
            selectImageDialog.dismiss();
        }
    }

    /**
     * 动态申请sdcard读写权限
     */
    private void autoObtainStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
            }
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }
    }

    /**
     * 申请访问相机权限
     */
    private void autoObtainCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    showToast(getResources().getString(R.string.you_have_deny));
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            } else {//有权限直接调用系统相机拍照
                if (hasSdcard()) {
                    mPicFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
                    imageUri = Uri.fromFile(mPicFile);
                    //通过FileProvider创建一个content类型的Uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(getContext(), mAuthority, mPicFile);
                    }
                    PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    showToast(getResources().getString(R.string.no_sd));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        mPicFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
                        imageUri = Uri.fromFile(mPicFile);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //通过FileProvider创建一个content类型的Uri
                            imageUri = FileProvider.getUriForFile(getContext(), mAuthority, mPicFile);
                        }
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        showToast(getResources().getString(R.string.no_sd));
                    }
                } else {
                    showToast(getResources().getString(R.string.please_open_camera));
                }
                break;
            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {
                    showToast(getResources().getString(R.string.please_open_sd));
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        switch (requestCode) {
            //相机返回
            case CODE_CAMERA_REQUEST:
                if (mPicFile != null) {
                    mLocalPicPathList.add(mPicFile.getAbsolutePath());
                    mAdapter.notifyDataSetChanged();
                }
                break;
            //相册返回
            case CODE_GALLERY_REQUEST:
                String path = PhotoUtils.getPath(getContext(), data.getData());
                File file = new File(path);
                if (file.length() > MAX_POST_SIZE) {
                    showToast("文件大小不能超过10M");
                    return;
                }
                if (mLocalPicPathList.contains(path)) {
                    showToast("该照片已经被选中，请重新选。");
                } else {
                    mLocalPicPathList.add(path);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case CODE_PIC_REQUEST:
                mLocalPicPathList = data.getStringArrayListExtra("pics");
                mLocalPicPathList.add(0, FRIST_PIC);
                mAdapter.setData(mLocalPicPathList);
                break;
            default:
        }
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 格式化字符串
     *
     * @param resId
     * @param args
     * @return
     */
    public String formatString(@StringRes int resId, Object... args) {
        return String.format(getString(resId), args);
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
