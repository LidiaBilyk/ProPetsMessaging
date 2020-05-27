package telran.ProPets.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@RefreshScope
public class MessagingConfiguration {
	@Value("${templateLogin}")
	String templateLogin;
	@Value("${templateId}")
	String templateId;
	@Value("${checkJwtUri}")
	String checkJwtUri;
	@Value("${complainUri}")
	String complainUri;
	@Value("${activityUri}")
	String activityUri;
}
