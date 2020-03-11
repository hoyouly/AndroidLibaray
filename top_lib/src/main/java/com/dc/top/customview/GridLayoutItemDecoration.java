package com.dc.top.customview;

/**
 * @ Time  :  2020-02-21
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by JackChen on 2018/3/3. GridView样式的分割线
 */
public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDriver;

    // 网上绝大部分用的系统的一个属性 叫做 android.R.attrs.listDriver ，这个也可以，需要在清单文件中配置
    public GridLayoutItemDecoration(Context context, int drawableRescourseId) {
        // 解析获取 Drawable
        mDriver = ContextCompat.getDrawable(context, drawableRescourseId);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 留出分割线的位置  每个item控件的下边和右边
        outRect.bottom = mDriver.getIntrinsicHeight();
        outRect.right = mDriver.getIntrinsicWidth();
    }

    // 绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 绘制分割线

        // 绘制水平方向
        drawHorizontal(c, parent);
        // 绘制垂直方向
        drawVertical(c, parent);
    }


    /**
     * 绘制垂直方向的分割线
     *
     * @param c
     * @param parent
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int top = childView.getTop() - params.topMargin;
            int bottom = childView.getBottom() + params.bottomMargin;
            int left = childView.getRight() + params.rightMargin;
            int right = left + mDriver.getIntrinsicWidth();

            //计算水平分割线的位置
            mDriver.setBounds(left, top, right, bottom);
            mDriver.draw(c);
        }
    }


    /**
     * 绘制水平方向的分割线
     *
     * @param c
     * @param parent
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int left = childView.getLeft() - params.leftMargin;
            int right = childView.getRight() + mDriver.getIntrinsicWidth() + params.rightMargin;
            int top = childView.getBottom() + params.bottomMargin;
            int bottom = top + mDriver.getIntrinsicHeight();

            //计算水平分割线的位置
            mDriver.setBounds(left, top, right, bottom);
            mDriver.draw(c);
        }
    }
}
