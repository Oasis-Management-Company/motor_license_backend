package com.app.IVAS.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author uhuegbulem chinomso
 * email: chimaisaac60@gmail.com
 * Oct, 2022
 **/
@Configuration
@ConfigurationProperties(prefix = "app", ignoreInvalidFields = true)
@Getter
@Setter
public class AppConfigurationProperties {
    private String jwtSecret;
    private int jwtExpiration;
    private String secretSalt;
    private String printDirectory;
    private String vansoUsername;
    private String vansoSender;
    private String vansoPassword;
    private String vansoUrl;
    private String token;
}
