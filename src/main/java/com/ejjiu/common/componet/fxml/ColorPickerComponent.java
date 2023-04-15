package com.ejjiu.common.componet.fxml;

import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.jpa.table.Config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/22 10:56
 */
public class ColorPickerComponent extends ColorPicker  implements AutowireInterface, ChangeListener<Color> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    public ColorPickerComponent() {
        super();
        this.valueProperty().addListener(this);
        this.initialize();
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null) {
            return;
        }
        String config = configRepository.getConfig(configType);
        
        setSelectedWithSave(config);
    }
    @FXML
    public void initConfigIfNoValue(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        final Config config = configRepository.findByKey(configType.name());
        
        if (config!=null) {
            String conf = configRepository.getConfig(config);
            setSelectedWithSave(conf);
        }
    }
    public final void setSelectedWithSave(String hexValue) {
        if (StringUtils.isEmpty(hexValue)) {
            return;
        }
        Color web = Color.web(hexValue);
        this.valueProperty().set(web);
        configRepository.setConfig(configType,web.toString());
    }
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
 
    @Override
    public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
        if (configRepository == null || configType == null) {
            return;
        }
        configRepository.setConfig(configType,newValue.toString());
    }
}
