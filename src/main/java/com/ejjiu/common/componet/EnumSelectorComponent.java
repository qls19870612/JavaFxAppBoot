package com.ejjiu.common.componet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.interfaces.EnumName;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.jpa.table.Config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;

/**
 *
 * 创建人  liangsong
 * 创建时间 2024/03/06 17:47
 */
public class EnumSelectorComponent extends MenuButton implements AutowireInterface {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    private Class<Enum> enumClass;
    private Method enumClassValueOfMethod;
    private Map<Enum, CheckBox> checkBoxMap = Maps.newHashMap();
    
    public List<Enum> getList() {
        return list;
    }
    
    public void setList(List<Enum> list) {
        this.list = list;
        this.updateView();
    }
    
    private List<Enum> list = Lists.newArrayList();
    
    public EnumSelectorComponent() {
        super();
        this.initialize((URL) null, (ResourceBundle) null);
    }
    
    @FXML
    public void initConfigIfNoValue(ConfigType configType) {
        this.configType = configType;
        if (this.configRepository != null && list.size() == 0) {
            Config config = this.configRepository.findByKey(configType.name());
            if (config != null) {
                if (StringUtils.isNotEmpty(config.getValue())) {
                    final String[] split = config.getValue().split(",");
                    
                    for (String s : split) {
                        
                        final Enum invoke;
                        try {
                            invoke = (Enum) enumClassValueOfMethod.invoke(null, s);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        list.add(invoke);
                    }
                }
            }
            
        } else {
            saveList();
        }
        updateView();
        
    }
    

    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        this.trySetData();
        
    }
    
    private void trySetData() {
        if (this.configRepository != null && enumClassValueOfMethod != null && configType != null) {
            Config config = this.configRepository.findByKey(configType.name());
            if (config != null) {
                if (StringUtils.isNotEmpty(config.getValue())) {
                    final String[] split = config.getValue().split(",");
                    
                    for (String s : split) {
                        
                        try {
                            Enum invoke = (Enum) enumClassValueOfMethod.invoke(null, s);
                            list.add(invoke);
                         
                        } catch (Exception e) {
                            //                            throw new RuntimeException(e);
                        }
                    }
                    
                    
                }
                
            }
            this.updateView();
        }
    }
    private void updateSelect() {
        for (CheckBox value : checkBoxMap.values()) {
            value.setSelected(false);
        }
        for (Enum anEnum : list) {
            final CheckBox checkBox = checkBoxMap.get(anEnum);
            if (checkBox != null) {
                checkBox.setSelected(true);
            }
        }
    }
    private void updateView()
    {
        updateLabel();
        updateSelect();
    }
    private void updateLabel() {
        
        if (list.size() > 0) {
            this.setText(StringUtils.join(list, ","));
        } else {
            this.setText("-----");
        }
    }
    
    public void setEnum(Class<Enum> enumClass) {
        this.enumClass = enumClass;
        try {
            this.enumClassValueOfMethod = this.enumClass.getDeclaredMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        boolean hasAlias = EnumName.class.isAssignableFrom(enumClass);
        
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            String name = hasAlias ? ((EnumName) enumConstant).aliasName() : enumConstant.name();
            final CheckBox node = new CheckBox(name);
            checkBoxMap.put(enumConstant, node);
            node.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        if (!list.contains(enumConstant)) {
                            list.add(enumConstant);
                        }
                    } else {
                        list.remove(enumConstant);
                    }
                    saveList();
                    updateLabel();
                }
            });
            final CustomMenuItem e = new CustomMenuItem(node, false);
            e.setUserData(enumConstant);
            this.getItems().add(e);
        }
        this.trySetData();
        
    }
    
    private void saveList() {
        configRepository.setConfig(configType.name(), StringUtils.join(list, ","));
    }
    
    public Class<Enum> getEnum() {
        return this.enumClass;
    }
    
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
}
