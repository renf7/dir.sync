package pl.dic.sync.client.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Getter;
import pl.dic.sync.client.service.DirSenderService;
import pl.dic.sync.client.service.ServerRestClientService;
 
@Component
public class WelcomePageController {
    @Getter @FXML private TextField srcPathField;
    @Getter @FXML private TextField destPathField;
    @Getter @FXML private TextField usernameField;
    @Autowired private ServerRestClientService serverRestClientService;
    @Autowired private DirSenderService dirSenderService;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        serverRestClientService.loginUser(usernameField.getText());
        dirSenderService.init(usernameField.getText(), srcPathField.getText());
        dirSenderService.listenDir();
    }

}
