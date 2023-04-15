package com.ejjiu.common.componet.fxml;


import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSONObject;
import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.interfaces.ISerializable;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


/**
 * 自动存储数据列表
 * 创建人  liangsong
 * 创建时间 2021/09/02 17:11
 */
@SuppressWarnings("unchecked")
public class SerializableListView<T extends ISerializable> extends ListView<T>  implements AutowireInterface {
    private static final Logger logger = LoggerFactory.getLogger(SerializableListView.class);
    @Autowired
    private ConfigRepository configRepository;
    private ConfigType configType;
    private Class<T> itemClass;

    public SerializableListView() {
        super();
        this.initialize();
    }

    public ConfigType getConfigType() {
        return configType;
    }
    @FXML
    public void setItemClass(Class<T> itemClass)
    {
        this.itemClass = itemClass;
        tryGetDataFillList();
    }
    public Class<T> getItemClass(){
        return this.itemClass;
    }
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        tryGetDataFillList();

    }
    public void fillList(List<String> dataList)
    {
        configRepository.setConfig(configType,JSONObject.toJSONString(dataList));
        updateList(dataList);
    }
    private void tryGetDataFillList() {
        if (this.itemClass == null) {
            return;
        }
        if (this.configType == null) {
            return;
        }
        
        String config = configRepository.getConfig(configType.name());
        if (StringUtils.isNotEmpty(config)) {
            List<String> strings;
            try {
                strings = JSONObject.parseArray(config, String.class);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                strings = Lists.newArrayList();
            }
            updateList(strings);
        }else
        {
            List<T> list = Lists.newArrayList();
            setItems(FXCollections.observableList(list));
        }
      
    }
    
    private void updateList(List<String> dataList) {
        List<T> list = Lists.newArrayList();
        for (String s : dataList) {
            T item  = createT(s);
            list.add(item);
        }
        setItems(FXCollections.observableList(list));
    }
    
    private T createT(String s) {
        try {
            ISerializable iSerializable =itemClass.newInstance();
            iSerializable.decode(s);
            return (T)iSerializable;
        }
        catch (Exception e)
        {
            logger.error("createT e:{}", e);
        }
        return null;
    }

    public void save()
    {
        if (configRepository == null || configType == null) {
            return;
        }
       
        configRepository.setConfig(configType, getJSONStringListData());
    }
    public String getJSONStringListData()
    {
        List<String> list = Lists.newArrayList();
        for (T item : getItems()) {
            String encode = item.encode();
            list.add(encode);
        }
        return JSONObject.toJSONString(list);
    }
}
