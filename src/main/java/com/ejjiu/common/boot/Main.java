package com.ejjiu.common.boot;

import com.ejjiu.common.config.AppConfig;
import com.ejjiu.common.controllers.Controller;
import com.ejjiu.common.startLoader.Constant;
import com.ejjiu.common.startLoader.PropertyReaderHelper;
import com.ejjiu.common.startLoader.SplashScreen;
import com.ejjiu.utils.LogCustom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static SplashScreen splashScreen;
    
    private static List<Image> icons = new ArrayList<>();
    
    private static Consumer<Throwable> errorAction = defaultErrorAction();
    
    private final List<Image> defaultIcons = new ArrayList<>();
    
    private final CompletableFuture<Runnable> splashIsShowing;
    
    public static String[] savedArgs = new String[0];
 
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private static Stage primaryStage;
    private ConfigurableApplicationContext applicationContext;
    
    /**
     * Default error action that shows a message and closes the app.
     */
    private static Consumer<Throwable> defaultErrorAction() {
        return e -> {
            Alert alert = new Alert(AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" + "Please contact your software vendor.\n\n" +
                    "The application will stop now.");
            alert.showAndWait().ifPresent(response -> Platform.exit());
        };
    }
    
    public Main() {
        splashIsShowing = new CompletableFuture<>();
        
    }
    
    public Collection<Image> loadDefaultIcons() {
        LogCustom logCustom = new LogCustom();
        logCustom.detach();
        Class<? extends Main> aClass = getClass();
        ClassLoader classLoader = aClass.getClassLoader();
        
        String baseUrl = "icons/";
        
        Image image = getImage(classLoader, baseUrl, getIconName("16x16.png"));
        return Arrays.asList(image, getImage(classLoader, baseUrl, getIconName("24x24.png")),
                             getImage(classLoader, baseUrl, getIconName("36x36.png")), getImage(classLoader, baseUrl, getIconName("42x42.png")),
                             getImage(classLoader, baseUrl, getIconName("64x64.png"))
                            );
    }
    
    protected String getIconName(String name) {
        return "icon_" + name;
    }
    
    private Image getImage(ClassLoader classLoader, String baseUrl, String s) {
        URL resource = classLoader.getResource(baseUrl + s);
        return new Image(resource.toExternalForm());
    }
    
    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception {
        
        
        // Load in JavaFx Thread and reused by Completable Future, but should no be a big deal.
        long startTime = System.currentTimeMillis();
        try {
            defaultIcons.addAll(loadDefaultIcons());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        Supplier<ConfigurableApplicationContext> supplier = () -> SpringApplication.run(SpringMain.class, savedArgs);
        CompletableFuture.supplyAsync(supplier).whenComplete((ctx, throwable) -> {
            
            applicationContext = ctx;
            initConfig();
            logger.debug("init currentTimeMillis:{}", System.currentTimeMillis() - startTime);
            if (throwable != null) {
                logger.error("Failed to load spring application context: ", throwable);
                Platform.runLater(() -> errorAction.accept(throwable));
            } else {
                Platform.runLater(() -> {
                    applyEnvPropsToView(ctx);
                    loadIcons(ctx);
                    //                    launchApplicationView(ctx);
                    
                });
            }
            
        }).thenAcceptBothAsync(splashIsShowing, (ctx, closeSplash) -> {
            Platform.runLater(closeSplash);
        });
    }
    
    private void initConfig() {
        AppConfig.initSqlLite();
    }
    
    private void loadIcons(ConfigurableApplicationContext ctx) {
        try {
            final List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), Constant.KEY_APPICONS);
            if (!fsImages.isEmpty()) {
                fsImages.forEach((s) -> {
                    Image img = new Image(getClass().getResource(s).toExternalForm());
                    icons.add(img);
                });
            } else { // add factory images
                icons.addAll(defaultIcons);
            }
        } catch (Exception e) {
            logger.error("Failed to load icons: ", e);
        }
    }
    
    /**
     * Apply env props to view.
     * @param ctx
     */
    private static void applyEnvPropsToView(ConfigurableApplicationContext ctx) {
        PropertyReaderHelper.setIfPresent(ctx.getEnvironment(), Constant.KEY_TITLE, String.class, primaryStage::setTitle);
        
        //        PropertyReaderHelper.setIfPresent(ctx.getEnvironment(), Constant.KEY_STAGE_WIDTH, Double.class, primaryStage::setWidth);
        //
        //        PropertyReaderHelper.setIfPresent(ctx.getEnvironment(), Constant.KEY_STAGE_HEIGHT, Double.class, primaryStage::setHeight);
        
        PropertyReaderHelper.setIfPresent(ctx.getEnvironment(), Constant.KEY_STAGE_RESIZABLE, Boolean.class, primaryStage::setResizable);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //        checkDbFolder();
        File file = new File("config");
        if (!file.exists()) {
            file.mkdirs();
        }
        this.primaryStage = primaryStage;
        final Stage splashStage = new Stage(StageStyle.TRANSPARENT);
        
        if (splashScreen.visible()) {
            final Scene splashScene = new Scene(splashScreen.getParent(), Color.TRANSPARENT);
            splashStage.setScene(splashScene);
            splashStage.getIcons().addAll(defaultIcons);
            splashStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle("启动");
            splashStage.setAlwaysOnTop(true);
            splashStage.show();
            splashScreen.show();
        }
        
        splashIsShowing.complete(() -> {
            try {
                initPrimaryStage(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            
            }
            
            splashScreen.hide(new Runnable() {
                @Override
                public void run() {
                    logger.debug("splashScreen.hide 1:{}", 1);
                    splashStage.hide();
                    splashStage.setScene(null);
                }
            });
        });
        
        
    }
    
    
    private void initPrimaryStage(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("/fxml/Main.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        loader.load();
        Controller controller = loader.getController();
        Parent root = loader.getRoot();
        
        final Scene value = new Scene(root);
        primaryStage.setScene(value);
        primaryStage.getIcons().addAll(icons);
        primaryStage.show();
        final Boolean maximized = applicationContext.getEnvironment().getProperty("javafx.stage.maximized", Boolean.class);
        primaryStage.setMaximized(maximized);
        final Double minWidth = applicationContext.getEnvironment().getProperty("javafx.stage.min.width", Double.class);
        final Double minHeight = applicationContext.getEnvironment().getProperty("javafx.stage.min.height", Double.class);
        if (minWidth != null) {
            primaryStage.setMinWidth(minWidth);
        }
        if (minHeight != null) {
            primaryStage.setMinHeight(minHeight);
        }
        
        //        root.getScene().getStylesheets().add(this.getClass().getResource("/css/listview.css").toExternalForm());
        //        URL resource1 = this.getClass().getResource("/css/JMetroDarkTheme.css");
        URL resource1 = this.getClass().getResource("/css/listview.css");
        String e = resource1.toExternalForm();
        root.getScene().getStylesheets().add(e);
        controller.setup();
        controller.onSelect();
        primaryStage.addEventHandler(WINDOW_CLOSE_REQUEST, event -> controller.onAppClose());
        
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
    
    
    public static void start(String[] args) {
        splashScreen = new SplashScreen();
        savedArgs = args;
    }
    
    
}
