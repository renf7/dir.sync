package pl.dic.sync.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import pl.dir.sync.common.dto.UserDto;

@Service
@Slf4j
public class ServerRestClientService {

    @Autowired
    private RestTemplate myRestTemplate;

    @Value("${dic.sync.server.url.register}")
    private String registerUrl;
    @Value("${dic.sync.server.url.all.registered}")
    private String allRegistered;

	public void loginUser(String user) {
        UserDto registerDto = new UserDto();
        registerDto.setUsername(user);

        myRestTemplate.postForObject(this.registerUrl, registerDto, Void.class);
        log.debug("user registred on server. Serwer will create kafka topic dedicated for user: " + user);
	}

	public UserDto[] allLoggedIn() {
        return myRestTemplate.getForObject(this.allRegistered, UserDto[].class);
	}
}
