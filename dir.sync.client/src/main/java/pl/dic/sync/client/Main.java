package pl.dic.sync.client;

import java.io.IOException;
import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.dic.sync.client.service.DirSenderService;

@SpringBootApplication
@EnableAsync
public class Main extends Application {

	private static String[] savedArgs;
	private ConfigurableApplicationContext context;
	
	@Override
	public void init() throws Exception {
		this.context = SpringApplication.run(Main.class, savedArgs);
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
			primaryStage.setScene(scene);
			primaryStage.show();
			DirSenderService dirSenderService = this.context.getBean(DirSenderService.class);
			dirSenderService.init(savedArgs[0], savedArgs[1]);
			dirSenderService.listenDir();
		} catch(IOException e) {
			throw new IllegalStateException("Unable to load view:", e);
		}
	}

	public static void main(String[] args) {
		savedArgs=args;
		launch(args);
	}

	@Bean
    public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("DirSyncThreadGroup-");
        executor.initialize();
        return executor;
    }
}
