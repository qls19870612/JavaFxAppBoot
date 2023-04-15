package com.ejjiu.common.componet.fxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 *
 * 创建人  liangsong
 * 创建时间 2023/04/14 10:54
 */

public class TitleComponent extends AnchorPane {
    
    private final Label label;
    private String backgroundColor;
    private String title;
    
    //    public TitleComponent(@NamedArg(value = "text", defaultValue = "") String text) {
    //        super();
    //        label = new Label(text);
    //        label.setMinHeight(20);
    //        this.label.setLayoutX(5);
    //        this.getChildren().add(label);
    //        this.setBackgroundColor("#bababa");
    //        this.setTitleHeight(26);
    //    }
    //
    public TitleComponent() {
        super();
        label = new Label("");
        label.setMinHeight(20);
        this.label.setLayoutX(5);
        this.getChildren().add(label);
        this.setBackgroundColor("#bababa");
        this.setTitleHeight(26);
    }
    
    public TitleComponent(Node... children) {
        super(children);
        label = new Label("");
        label.setMinHeight(20);
        this.label.setLayoutX(5);
        this.getChildren().add(label);
        this.setBackgroundColor("#bababa");
        this.setTitleHeight(26);
    }
    
    @FXML
    public void setTitle(String label) {
        this.title = label;
        this.label.setText(label);
    }
    
    public String getTitle() {
        return title;
    }
    
    @FXML
    public void setBackgroundColor(String s) {
        this.backgroundColor = s;
        this.styleProperty().setValue("-fx-background-color: " + s);
    }
    
    public double getTitleHeight() {
        return this.getMinHeight();
    }
    
    public String getBackgroundColor() {
        return this.backgroundColor;
    }
    
    @FXML
    public void setTitleHeight(double value) {
        this.setMaxHeight(value);
        this.setMinHeight(value);
        this.label.setLayoutY((value - label.getMinHeight()) * 0.5);
    }
}
