package io.github.wong1988.transmit.base;

import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.wong1988.adapter.BaseListAdapter;
import io.github.wong1988.adapter.attr.LoadState;
import io.github.wong1988.adapter.listener.OnLoadMoreListener;
import io.github.wong1988.adapter.listener.OnStateFooterClickListener;


/**
 * 封装的ListActivity，可继承进行扩展
 */
public abstract class BaseListFragment<BEAN, ADAPTER extends BaseListAdapter<BEAN>> extends BaseFragment {

    // 初始页码
    private int INITIAL_PAGE_NUMBER;

    // 当前页数
    private int currentPage = 0;
    // 总页数
    private int totalPage = 1;
    // 适配器（单布局）
    protected ADAPTER mAdapter;

    // 此属性只针对上拉加载使用
    // success和fail时一定要重置为true
    public boolean isCanRequest = false;

    // rv
    private RecyclerView mRv;

    // 布局管理器
    private RecyclerView.LayoutManager mManager;

    private boolean isFirstLoad = true;

    /**
     * 加载出错
     * 只有脚布局才会有监听
     */
    public class MySimpleLayoutErrorListener implements OnStateFooterClickListener {

        @Override
        public void onClick(LoadState state) {
            if (state == LoadState.LOAD_ERROR) {
                ++currentPage;

                if ((INITIAL_PAGE_NUMBER == 0 ? currentPage + 1 : currentPage) > totalPage) {

                    if (mAdapter != null)
                        mAdapter.setLoadState(LoadState.LOAD_END);

                } else {

                    if (mAdapter != null)
                        mAdapter.setLoadState(LoadState.LOADING);

                    if (currentPage == INITIAL_PAGE_NUMBER) {
                        // 模拟下拉
                        onPullDown();
                    } else {
                        requestData(currentPage);
                    }
                }
            }
        }
    }

    @Override
    protected void initList() {

        INITIAL_PAGE_NUMBER = startPagerNumber() == PagerStartType.ZERO ? 0 : 1;

        mRv = initRecyclerView();
        mManager = initLayoutManager();
        mRv.setLayoutManager(mManager);
        mAdapter = initAdapter();

        if (mRv == null)
            throw new RuntimeException("RecyclerView/ShimmerRecyclerView = null.");

        if (mAdapter == null)
            throw new RuntimeException("Adapter = null.");

        if (mManager == null)
            throw new RuntimeException("LayoutManager == null.");

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (!isCanRequest)
                    return;

                ++currentPage;

                if ((INITIAL_PAGE_NUMBER == 0 ? currentPage + 1 : currentPage) > totalPage) {

                    if (mAdapter != null)
                        mAdapter.setLoadState(LoadState.LOAD_END);

                } else {

                    if (mAdapter != null)
                        mAdapter.setLoadState(LoadState.LOADING);

                    isCanRequest = false;
                    requestData(currentPage);
                }

            }
        });


        mRv.setAdapter(mAdapter);
        mAdapter.setOnStateFooterClickListener(new MySimpleLayoutErrorListener());

        if (initItemDecoration() != null) {
            mRv.addItemDecoration(initItemDecoration());
        }

        mAdapter.setLoadState(LoadState.LOADING);
        initListener();
    }

    //  第一页的开始int
    protected abstract PagerStartType startPagerNumber();

    //  是否立马清空第一页数据（ eg. 删除记录 立马清空数据）
    protected abstract boolean clearDataNow();

    //  初始化列表
    protected abstract RecyclerView initRecyclerView();

    //  初始化列表的管理器
    protected abstract RecyclerView.LayoutManager initLayoutManager();

    protected abstract RecyclerView.ItemDecoration initItemDecoration();

    //  初始化列表的适配器
    protected abstract ADAPTER initAdapter();

    //  初始化列表的监听
    protected abstract void initListener();

    //  请求第一页数据
    protected abstract void requestData(final int page);

    protected void successNeedTodo() {

        isCanRequest = true;

        if (mAdapter != null)
            mAdapter.setLoadState(LoadState.LOAD_COMPLETE);

    }

    /**
     * 出错时需要调用此方法
     */
    protected void failNeedTodo() {

        isCanRequest = true;

        if (currentPage >= INITIAL_PAGE_NUMBER)
            currentPage--;

        if (mAdapter != null)
            mAdapter.setLoadState(LoadState.LOAD_ERROR);

    }

    /**
     * 出错时需要调用此方法
     */
    protected void failNeedTodo(String msg) {

        isCanRequest = true;

        if (currentPage >= INITIAL_PAGE_NUMBER)
            currentPage--;

        if (mAdapter != null)
            mAdapter.setLoadState(LoadState.LOAD_ERROR);

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 检查 是否清空数据
     */
    private void needToCheckPageOne(int requestPage, List<BEAN> list, int pageSize) {
        if (requestPage == INITIAL_PAGE_NUMBER) {
            mAdapter.clearAll();
            totalPage = (pageSize == 0 ? 1 : pageSize);
            currentPage = INITIAL_PAGE_NUMBER;
            if (list == null || list.size() == 0) {
                mAdapter.setLoadState(LoadState.LOAD_NO_DATA);
            }
        }
    }

    @Override
    public void loadData() {
        onPullDown();
        isFirstLoad = false;
    }

    // 添加数据的方法
    protected void addData(int requestPage, List<BEAN> list, int pageSize) {
        successNeedTodo();
        needToCheckPageOne(requestPage, list, pageSize);
        mAdapter.addData(list);
    }

    // 添加不分页数据的方法
    protected void addNoPagingData(List<BEAN> list) {
        addData(startPagerNumber() == PagerStartType.ZERO ? 0 : 1, list, 1);
        if (mAdapter != null && mAdapter.getAttachDataSize() > 0)
            mAdapter.setLoadState(LoadState.LOAD_END);
    }

    protected void failNeedTodoNoPaging() {
        if (currentPage >= INITIAL_PAGE_NUMBER)
            currentPage--;

        if (mAdapter != null && mAdapter.getAttachDataSize() == 0) {
            mAdapter.setLoadState(LoadState.LOAD_ERROR);
        }
    }

    /**
     * 手动下拉刷新时 使用
     * 模拟下拉刷新时使用
     * （支持脚布局 - 显示下拉刷新
     * 否则 - 显示下拉刷新、立马关闭刷新 显示占位view）
     */
    public void onPullDown() {

        if (mAdapter == null)
            return;

        if (clearDataNow()) {
            mAdapter.clearAll();
            currentPage = INITIAL_PAGE_NUMBER;
            totalPage = 1;
        }

        if (mAdapter.getAttachDataSize() == 0)
            mAdapter.setLoadState(LoadState.LOADING);

        requestData(INITIAL_PAGE_NUMBER);
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
}

