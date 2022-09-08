package com.ejjiu.common.enums;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用class而不使用emum是因为可以扩展
 * 创建人  liangsong
 * 创建时间 2019/10/23 18:21
 */
public class ConfigType {
    public static final Map<String, ConfigType> maps = new HashMap<>();
    public static final ConfigType TAB_SELECT_INDEX = new ConfigType("TAB_SELECT_INDEX");
    
    public static final ConfigType GLOBAL_GIT_BASH_PATH = new ConfigType("GLOBAL_GIT_BASH_PATH");
    
    /**
     * 供javafx fxml里使用字符串获取ConfigType常量
     * @param config
     * @return
     */
    public static ConfigType valueOf(String config) {
        ConfigType configType = maps.get(config);
        if (configType == null) {
            throw new RuntimeException("组件构造函数必需是已经构造出来的ConfigType实例，你可能需要使用ConfigType.initExtends来扩展ConfigType常量");
        }
        return configType;
    }
    
 
    
    public static void initExtends(Class c) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method init = c.getDeclaredMethod("init");
        init.invoke(c);
        
    }
    
    public String name() {
        return _name;
    }
    
    private final String _name;
    
    
    public ConfigType(String name) {
        
        this._name = name;
        ConfigType.maps.put(name, this);
    }
}

