package com.ejjiu.common.componet.fxml;

import com.google.common.collect.Lists;

import com.ejjiu.common.componet.render.EnumRender;
import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.jpa.table.Config;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/14 9:50
 */
public class ComboboxComponent<T> extends ComboBox<T> implements AutowireInterface, ChangeListener<Number> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    
   
    
    private Class<T> enumClass;
    
    public ComboboxComponent() {
        super();
        this.getSelectionModel().selectedIndexProperty().addListener(this);
        this.initialize();
    }
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null) {
            return;
        }
        int config = configRepository.getInt(configType);
        this.getSelectionModel().select(config);

    }
    public Class<T> getEnumClass() {
        return enumClass;
    }
    @FXML
    public void setEnumClass(Class<T> enumClass)
    {
        this.enumClass = enumClass;
        if (enumClass!=null) {
            this.setCellFactory(param ->  new EnumRender());
            final T[] enumConstants = (T[])enumClass.getEnumConstants();
            this.setItems(FXCollections.observableList(Lists.newArrayList(enumConstants)));
            this.setButtonCell(new EnumRender(Color.BLACK));
        }
    }
    @FXML
    public void initConfigIfNoValue(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        final Config config = configRepository.findByKey(configType.name());
        
        if (config!=null) {
            int conf = configRepository.getInt(configType);
            this.getSelectionModel().select(conf);
        }
    }
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (configRepository == null || configType == null) {
            return;
        }
        configRepository.setInt(configType,newValue.intValue());
        this.layout();
    }
}
