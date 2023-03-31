package com.ejjiu.common.componet.fxml;

import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.jpa.table.Config;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/23 17:42
 */
public class ChoiceBoxComponent<T> extends ChoiceBox<T> implements AutowireInterface, ChangeListener<Number> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    public ChoiceBoxComponent(@NamedArg(value = "configType") ConfigType configType) {
        super();
        this.getSelectionModel().selectedIndexProperty().addListener(this);
        this.initialize();
        setConfigType(configType);
    }
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null || configType == null) {
            return;
        }
        int config = configRepository.getInt(configType);
        this.getSelectionModel().select(config);
        
    }
    @FXML
    public void initConfigIfNoValue(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        final Config config = configRepository.findByKey(configType.name());
        
        if (config!=null) {
            int conf = configRepository.getInt(config);
            this.getSelectionModel().select(conf);
        }
    }
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (configRepository==null || configType == null) {
            return;
        }
        configRepository.setInt(configType,newValue.intValue());
    }
}
