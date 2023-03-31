package com.ejjiu.common.controllers;



import com.google.common.collect.Lists;

import javafx.scene.control.Tab;
import com.ejjiu.common.interfaces.ITab;

import java.util.List;


/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/13 16:30
 */
public abstract class AbstractController implements ITab {
    private Tab tab;
    private List<AbstractController> subControllers = Lists.newArrayList();
    @Override
    public Tab getTab() {
        return tab;
    }

    @Override
    public ITab setTab(Tab tab) {
        this.tab = tab;
        return this;
    }
    public void addSubController(AbstractController controller)
    {
        this.subControllers.add(controller);
    }

    /**
     * 只执行一次
     */
    public void setup(){
        if (subControllers.size() > 0) {
            for (AbstractController subController : subControllers) {
                subController.setup();
            }
        }
    }

    @Override
    public void onSelect() {
        if (subControllers.size() > 0) {
            for (AbstractController subController : subControllers) {
                subController.onSelect();
            }
        }
    }

    @Override
    public void onAppClose() {
        if (subControllers.size() > 0) {
            for (AbstractController subController : subControllers) {
                subController.onAppClose();
            }
        }
    }
}
