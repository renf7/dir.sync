package pl.dic.sync.client;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.dic.sync.client.controler.WelcomePageController;
import pl.dic.sync.client.service.DirSenderService;

@SpringBootApplication
@EnableAsync
public class Main extends Application {

	private static String username;
	private static String srcPath;
	private static String destPath;
	private ConfigurableApplicationContext context;
	
	@Override
	public void init() throws Exception {
		this.context = SpringApplication.run(Main.class);
		// DirSenderService dirSenderService = this.context.getBean(DirSenderService.class);
		// dirSenderService.init(username, srcPath);
		// dirSenderService.listenDir();
	}

	@Override
	public void stop() throws Exception {
		DirSenderService dirSenderService = this.context.getBean(DirSenderService.class);
		dirSenderService.stopListenDir();
		context.close();
		System.gc();
		System.runFinalization();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader=new FXMLLoader(getClass().getResource("view/Main.fxml"));
			loader.setControllerFactory(this.context::getBean);
			Parent root = loader.load();
			Scene scene = new Scene(root,900,500);
			primaryStage.setTitle("Directory Sync Client");
			primaryStage.setScene(scene);
			initUiInputsText();
			primaryStage.show();
		} catch(IOException e) {
			throw new IllegalStateException("Unable to load view:", e);
		}
	}

	private void initUiInputsText() {
		WelcomePageController welcomePageController = this.context.getBean(WelcomePageController.class);
		welcomePageController.getUsernameField().setText(username);
		welcomePageController.getSrcPathField().setText(srcPath);
		welcomePageController.getDestPathField().setText(destPath);
	}


	public static void main(String[] args) {
		if (args.length > 0) {
			username = args[0];
		} else {
			username = "annymous";
		}
		if (args.length > 1) {
			srcPath = args[1];
		} else {
			File dir = new File("dic.sync.src");
			if (!dir.exists()) {
				dir.mkdir();
			}
			srcPath = dir.getAbsolutePath();
		}
		if (args.length > 2) {
			destPath = args[2];
		} else {
			destPath = System.getProperty("user.home");
		}
		launch();
	}
}
