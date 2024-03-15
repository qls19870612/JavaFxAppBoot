package com.ejjiu.common.controllers;

import com.ejjiu.common.enums.ConfigType;
import com.ejjiu.utils.Utils;
import com.sun.javafx.tk.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

public class Controller extends AbstractTabController {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static int count = 0;



    @FXML
    public Label timeLabel;
    @FXML
    public Label infoLabel;
    @FXML
    public TabPane tabPanel;
    @FXML
    public AnchorPane rootPanel;
    
    public static Controller getInstance() {
        return instance;
    }
    
    private static Controller instance = null;
    static Pattern logPatter = Pattern.compile("\\{}");
    private SimpleDateFormat timeDataFormat;
    public static void logAndPrint(String... args) {
        if (args.length > 0) {
            String first = args[0];
            if (first.contains("{}")) {
                args = new String[]{replaceAsLog(first,args)};
            }
        }
        logger.debug("logAndPrint args:{}", (Object) args);
        log(args);
    }
    
    private static String replaceAsLog(String first, String[] args) {
        final Matcher matcher = logPatter.matcher(first);
        int index = 1;
        StringBuffer sb = new StringBuffer();
        while (args.length > index && matcher.find())
        {
            matcher.appendReplacement(sb,args[index]);
            index++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    public static void log(String... args) {
        if (!Toolkit.getToolkit().isFxUserThread()) {
            Platform.runLater(() -> showLogInFxThread(args));
            return;
        }
        showLogInFxThread(args);
    }


    private static void showLogInFxThread(String[] args) {
        String showTxt = null;
        if (args.length == 1) {
            showTxt = args[0];
        } else if (args.length > 1) {
            StringBuilder stringBuffer = new StringBuilder();
            for (String arg : args) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(">");
                }
                stringBuffer.append(arg);
            }
            showTxt = stringBuffer.toString();
        } else {
            return;
        }
        //        if (showTxt.length() > 70) {
        //            showTxt = showTxt.substring(0, 30) + "...." + showTxt.substring(showTxt.length() - 40);
        //        }
        instance.infoLabel.setText(showTxt);
        if (instance.infoLabel.getTooltip() == null) {

            Tooltip toolTip = new Tooltip();
            Utils.hackTooltipStartTiming(toolTip);
            instance.infoLabel.setTooltip(toolTip);
        }
        instance.infoLabel.getTooltip().setText(showTxt);
        count++;
        instance.timeLabel.setText(count + ": " + instance.timeDataFormat.format(new Date()));
    }



    @Override
    public void setup() {
        instance = this;
        super.setup();
        setup(tabPanel, ConfigType.TAB_SELECT_INDEX);

        timeDataFormat = new SimpleDateFormat("HH:mm:ss");
     

        infoLabel.setText("");
        timeLabel.setText("");
    }

}
