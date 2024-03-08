package com.ejjiu.common.componet.fxml.date;

import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.common.interfaces.AutowireInterface;
import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.jpa.table.Config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * @beLongProjecet: JavaFx-DateTimePicker
 * @description: DateTimePicker plugin
 * @author: wzy
 * @createTime: 2023/04/18 18:10
 */
public class DateTimePicker extends HBox implements AutowireInterface {
    private final DateTimeFormatter formatter;
    // DateTime value
    private ObjectProperty<LocalDateTime> dateTime;
    private final Popup popupContainer;
    private final DateTimePickerSelect dateTimePickerSelect;
    public Boolean showLocalizedDateTime = false;
    @FXML
    private TextField textField;
    @FXML
    private Button button;
    @Autowired
    ConfigRepository configRepository;
    
    public DateTimePicker() {
        if (showLocalizedDateTime) {
            this.dateTime = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
        }
        else
        {
            this.dateTime = new SimpleObjectProperty<>();
        }
        this.formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        this.popupContainer = new Popup();
        this.dateTimePickerSelect = new DateTimePickerSelect(this);
        // Load FXML
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/date/DateTimePicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            // Should never happen.  If it does however, we cannot recover
            // from this
            throw new RuntimeException(ex);
        }
        this.button.setOnAction(this::handleButtonAction);
    }
    
    private ConfigType configType;
    
    @FXML
    public void initConfigIfNoValue(ConfigType configType) {
        this.configType = configType;
        if (this.configRepository != null && (dateTime == null || dateTime.isNull().getValue())) {
            Config config = this.configRepository.findByKey(configType.name());
            if (config != null) {
                if (StringUtils.isNotEmpty(config.getValue())) {
                    setTimeProperty(LocalDateTime.parse(config.getValue(),this.formatter));
                }
            }
            
        }
        
    }
    
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        trySetData();
    }
    
    private void trySetData() {
        if (this.configRepository != null && configType != null) {
            Config config = this.configRepository.findByKey(configType.name());
            if (config != null) {
                if (StringUtils.isNotEmpty(config.getValue())) {
                    setTimeProperty(LocalDateTime.parse(config.getValue()));
                }
            }
        }
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutowireInterface.super.initialize(null, null);
        if (showLocalizedDateTime) {
            textField.setText(formatter.format(dateTime.get()));
        }
        //        dateTime.addListener(
        //                (observable, oldValue, newValue) -> {
        //                    textField.setText(formatter.format(newValue));
        //                });
        button.prefHeightProperty().bind(textField.heightProperty());
        popupContainer.getContent().add(dateTimePickerSelect);
        popupContainer.autoHideProperty().set(true);
    }
    
    @FXML
    void handleButtonAction(ActionEvent event) {
        if (popupContainer.isShowing()) {
            popupContainer.hide();
        } else {
            final Window window = button.getScene().getWindow();
            final double x = window.getX() + textField.localToScene(0, 0).getX() + textField.getScene().getX();
            final double y = window.getY() + button.localToScene(0, 0).getY() + button.getScene().getY() + button.getHeight();
            popupContainer.show(this.getParent(), x, y);
            dateTimePickerSelect.upDataCalendar(true);
        }
    }
    
    /**
     * Gets the current LocalDateTime value
     * @return The current LocalDateTime value
     */
    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }
    
    /**
     * @Description: setTimeProperty
     * @Params
     * @Return
     * @Author wzy
     * @Date 2023/4/19 11:11
     **/
    public void setTimeProperty(LocalDateTime localDateTime) {
        if (this.dateTime == null) {
            
            this.dateTime = new SimpleObjectProperty<LocalDateTime>(localDateTime);
        }
        else
        {
            this.dateTime.setValue(localDateTime);
        }
        
        final LocalDateTime temporal = this.dateTime.get();
        if (temporal!=null) {
            
            textField.setText(formatter.format(temporal));
        }
        else
        {
            textField.setText("");
        }
        saveDate();
    }
    
    private void saveDate() {
        configRepository.setConfig(configType,textField.getText());
    }
    
    /**
     * @Description: clearTimeProperty
     * @Params
     * @Return
     * @Author wzy
     * @Date 2023/4/19 11:11
     **/
    public void clearTimeProperty() {
        this.dateTime.setValue(null);
        textField.setText("");
        saveDate();
    }
    
    public void hide() {
        if (popupContainer.isShowing()) {
            popupContainer.hide();
        }
    }
    
    public Boolean getShowLocalizedDateTime() {
        return showLocalizedDateTime;
    }
    
    public void setShowLocalizedDateTime(Boolean show) {
        this.showLocalizedDateTime = show;
        if (show) {
            setTimeProperty(LocalDateTime.now());
        }
        
    }
}
