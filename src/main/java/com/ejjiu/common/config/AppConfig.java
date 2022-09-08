package com.ejjiu.common.config;

import com.ejjiu.common.jpa.ConfigRepository;
import com.ejjiu.common.spring.utils.SpringUtil;




public class AppConfig {
 
 
 

    public static ConfigRepository getConfigRepository() {
        return configRepository;
    }

    private static ConfigRepository configRepository;
 
 
 

    public static void initSqlLite() {

        configRepository = (ConfigRepository) SpringUtil.getBean("configRepository");

    }
 


}
