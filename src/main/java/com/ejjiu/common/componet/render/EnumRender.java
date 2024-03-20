package com.ejjiu.common.componet.render;

import com.ejjiu.common.interfaces.EnumName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * 创建人  liangsong
 * 创建时间 2024/03/12 13:40
 */
public class EnumRender<T extends Enum> extends ListCell<T> {
    private final Paint textColor;
    
    public EnumRender() {
        super();
        textColor = null;
    }
    
    public EnumRender(Paint textColor) {
        super();
        this.textColor = textColor;
        setTextFill(textColor);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(EnumRender.class);
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            
            if (item instanceof EnumName) {
                setText(((EnumName) item).aliasName());
            } else {
                setText(item.name());
            }
            
            
        } else {
            setText("");
        }
    }
    
    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        if (textColor == null) {
            if (selected) {
                setTextFill(Color.DARKGREEN);
            } else {
                setTextFill(Color.DIMGREY);
            }
        }
    }
}
