package pl.dic.sync.client.controler;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.dic.sync.client.service.FileSenderService;
import pl.dic.sync.client.service.ServerRestClientService;
import pl.dic.sync.client.service.UserConsumerService;
 
@Component
@Slf4j
public class WelcomePageController {
    @Getter @FXML private TextField srcPathField;
    @Getter @FXML private TextField destPathOrShareFileField;
    @Getter @FXML private TextField usernameField;
    @FXML private TextField filenameField;
    @FXML private Button actionButton;
    @FXML private Label usernameLabel;
    @FXML private Label specialLabel;
    @Autowired private ServerRestClientService serverRestClientService;
    @Autowired private FileSenderService fileSenderService;
    @Autowired private UserConsumerService userConsumerService;

    private boolean differentUserSync;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        if (this.differentUserSync) {
            String username = usernameField.getText();
            if (Arrays.stream(serverRestClientService.allLoggedIn()).anyMatch(u -> u.getUsername().equals(username))) {
                fileSenderService.sendFile(srcPathField.getText(), destPathOrShareFileField.getText(), username);
            } else {
                log.warn("Cannot send request to user not logged in");
            }
        } else {
            String username = usernameField.getText();
            serverRestClientService.loginUser(username);
            userConsumerService.consumeTopic(username, destPathOrShareFileField.getText());
            fileSenderService.listenDir(username, srcPathField.getText());
            
            actionButton.setText("Synchronize with given user");
            usernameLabel.setText("Share with user");
            specialLabel.setText("File to share");
            destPathOrShareFileField.setText("");
            this.differentUserSync = true;
        }
    }

}
