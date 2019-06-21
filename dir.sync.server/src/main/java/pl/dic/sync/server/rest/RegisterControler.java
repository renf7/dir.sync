package pl.dic.sync.server.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// import kafka.admin.AdminUtils;
import pl.dir.sync.common.UserDto;

@RestController
@RequestMapping("/register")
public class RegisterControler {
    private static final int DESIRED_PARTITION_NUMBER = 5;
    @Autowired
    private AdminClient adminClient;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void register(@RequestBody UserDto userDto) throws InterruptedException, ExecutionException {
        String topicName = userDto.getUsername();
        if (!topicExists(topicName)) {
            createNewTopicWith5Part(topicName);
        }
    }

	@RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<UserDto> getAllRegistred() {
    	List<UserDto> allRegistred = new ArrayList<>();
        return allRegistred;
    }
    private boolean topicExists(String topicName) throws InterruptedException, ExecutionException {
        ListTopicsResult allTopics = adminClient.listTopics();
        if (allTopics != null && allTopics.names() != null) {
            return allTopics.names().get().contains(topicName);
        } else {
            return false;
        }
    }
    private void createNewTopicWith5Part(String topicName) {
        Collection<NewTopic> newTopics = new ArrayList<>();
        NewTopic topic = new NewTopic(topicName, DESIRED_PARTITION_NUMBER, (short) 1);
        newTopics.add(topic);
        adminClient.createTopics(newTopics);
    }
}
