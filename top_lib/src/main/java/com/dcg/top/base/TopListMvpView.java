package com.dcg.top.base;

import java.util.List;

/**
 * 带有列表的一些功能
 */

public interface TopListMvpView<T> extends TopMvpView {

    void loadData(List<T> datas, boolean hasMore);

    void saveData(Object o);

    void showEmptyView(boolean isShow);

    void appendData(T data);
}
