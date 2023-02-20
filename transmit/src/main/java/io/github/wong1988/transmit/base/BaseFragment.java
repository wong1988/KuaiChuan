package io.github.wong1988.transmit.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(layoutResId(), container, false);
        // 初始化
        init(inflate);
        // 初始化完毕后 初始化列表相关
        initList();
        return inflate;
    }

    protected abstract int layoutResId();

    protected abstract void init(View rootView);

    protected void initList() {

    }

    public abstract void loadData();

    // TODO 对外提供是否第一次请求数据
    // TODO 需求：切换Tab 或 滑动viewPager都需要刷新数据

    /**
     * vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
     * <p>
     * boolean defaultFirst = true;
     *
     * @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
     * super.onPageScrolled(position, positionOffset, positionOffsetPixels);
     * }
     * @Override public void onPageSelected(int position) {
     * super.onPageSelected(position);
     * <p>
     * ((RadioButton) rg.getChildAt(position)).setChecked(true);
     * <p>
     * // 下面的判断 是用来 每次重新刷新数据使用 如果 无此需求 下方判断 注释即可
     * <p>
     * boolean firstLoad = ((ParentFragment) fragments.get(position)).isFirstLoad();
     * <p>
     * if (defaultFirst && position == 0){
     * defaultFirst = false;
     * return;
     * }
     * <p>
     * if (!firstLoad){
     * ((ParentFragment) fragments.get(position)).loadData();
     * }
     * }
     * @Override public void onPageScrollStateChanged(int state) {
     * super.onPageScrollStateChanged(state);
     * }
     * });
     * <p>
     * rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
     * @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
     * <p>
     * int position = 0;
     * <p>
     * for (int i = 0; i < rg.getChildCount(); i++) {
     * RadioButton btn = (RadioButton) rg.getChildAt(i);
     * if (btn.getId() == checkedId) {
     * position = i;
     * break;
     * }
     * }
     * <p>
     * vp.setCurrentItem(position);
     * }
     * });
     * }
     */
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            loadData();
        }
    }
}
