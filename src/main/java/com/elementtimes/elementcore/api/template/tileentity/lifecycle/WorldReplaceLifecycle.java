package com.elementtimes.elementcore.api.template.tileentity.lifecycle;

import com.elementtimes.elementcore.api.template.tileentity.interfaces.IMachineLifecycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用于替换操作
 * @author luqin2007
 */
public abstract class WorldReplaceLifecycle<ELEMENT, REPLACE> implements IMachineLifecycle {

    protected List<Wrapper> mElements = new ArrayList<>();
    protected int mInterval = 0;
    protected List<ELEMENT> mElementFind = new ArrayList<>();

    /**
     * 从世界中寻找可替代的元素，将其添加到 List<ELEMENT> elements 中
     * 当该列表为空时，机器不运行
     * @param elements 可替代的元素
     */
    public abstract void findElements(List<ELEMENT> elements);

    /**
     * 获取两次寻找之间的冷却时间
     * @return 寻找冷却
     */
    public abstract int getFindInterval();

    /**
     * 将原本可替代的元素转换为新的元素
     * @param from 原元素
     * @return 新元素
     */
    public abstract REPLACE convert(ELEMENT from);

    /**
     * 生成新元素
     * @param element 原元素及其移除时间
     * @param to 新元素
     */
    public abstract void placeNewElement(Wrapper element, REPLACE to);

    /**
     * 从世界中移除旧元素
     * @param element 原元素及其移除时间
     */
    public abstract void removeOldElement(Wrapper element);

    /**
     * 收集新元素
     * @param element 原元素及其移除时间
     */
    public abstract void collectElement(Wrapper element);

    /**
     * 存储必要的数据
     * 注意：该方法会在每 tick 结束时主动调用，但恢复数据需要手动调用 loadSavedData 方法
     * @param interval 搜索间隔
     * @param elements 移除中的元素
     */
    public abstract void save(int interval, List<Wrapper> elements);

    /**
     * 恢复数据，该方法需要手动调用
     * @param interval 搜索间隔
     * @param elements 移除中的元素
     */
    public void loadSavedData(int interval, List<Wrapper> elements) {
        mInterval = interval;
        mElements.clear();
        mElements.addAll(elements);
        mElementFind.clear();
    }

    @Override
    public boolean onLoop() {
        // find
        mInterval++;
        if (mInterval >= getFindInterval()) {
            findElements(mElementFind);
            mInterval = 0;
            mElementFind.forEach(element -> mElements.add(new Wrapper(element)));
            mElementFind.clear();
        }
        // replace
        Iterator<Wrapper> iterator = mElements.iterator();
        while (iterator.hasNext()) {
            Wrapper element = iterator.next();
            element.tick++;
            removeOldElement(element);
            collectElement(element);
            if (element.isRemoved) {
                REPLACE convert = convert(element.element);
                placeNewElement(element, convert);
                iterator.remove();
            }
        }
        return true;
    }

    @Override
    public boolean onCheckFinish() {
        return mElementFind.isEmpty();
    }

    @Override
    public void onTickFinish() {
        save(mInterval, mElements);
    }

    public class Wrapper {
        ELEMENT element;
        int tick = 0;
        boolean isRemoved = false;

        Wrapper(ELEMENT element) {
            this.element = element;
        }
    }
}
