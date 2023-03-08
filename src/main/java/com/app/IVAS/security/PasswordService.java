package com.app.IVAS.security;

import com.app.IVAS.configuration.AppConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author uhuegbulem chinomso
 * email: chimaisaac60@gmail.com
 * Oct, 2022
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {
    private final AppConfigurationProperties appConfigurationProperties;

    public String hashPassword(String password){
       return BCrypt.hashpw(password, appConfigurationProperties.getSecretSalt());
    }

    public Boolean comparePassword(String userPassword, String providedPassword){
       String encryptedPassword = hashPassword(providedPassword);
       return userPassword.equals(encryptedPassword);
    }
}
