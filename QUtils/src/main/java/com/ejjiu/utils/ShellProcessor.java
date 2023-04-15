package com.ejjiu.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 *
 * 创建人  liangsong
 * 创建时间 2021/07/15 14:25
 */

public class ShellProcessor {
    public static class ShellExecuteException extends Exception {
        public ShellExecuteException(String message) {
            super(message);
        }
    }
    
    public interface ShellLogCallBack {
        void onLog(String log);
        
        void onError(String log);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ShellProcessor.class);
    
    public static void exe(String shellFile, String bashPath, ShellLogCallBack logCallBack)
            throws ShellExecuteException, IOException, InterruptedException {
        
        if (StringUtils.isEmpty(bashPath)) {
            throw new ShellExecuteException("未配置正确的git-bash.exe路径");
        }
        File file = new File(bashPath);
        if (!file.exists() || file.isDirectory()) {
            throw new ShellExecuteException("未配置正确的git-bash.exe路径");
        }
        if (StringUtils.isEmpty(shellFile)) {
            throw new ShellExecuteException("不正确的shell脚本文件路径");
        }
        File shFile = new File(shellFile);
        if (!shFile.exists() || shFile.isDirectory()) {
            throw new ShellExecuteException("不正确的shell脚本文件路径");
        }
        ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath(), shellFile);
        
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        pb.directory(shFile.getParentFile());
        int runningStatus = 0;
        pb.redirectErrorStream(false);
        
        Process p = pb.start();
        
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                
                String s = null;
                try {
                    while ((s = stdInput.readLine()) != null) {
                        if (logCallBack != null) {
                            logCallBack.onLog(s);
                        }
                    }
                    while ((s = stdError.readLine()) != null) {
                        
                        if (logCallBack != null) {
                            logCallBack.onError(s);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        }, "shell").start();
        
        runningStatus = p.waitFor();
        
        
        if (runningStatus != 0) {
            logCallBack.onError("执行发生错误");
        }
        
    }
    
}
