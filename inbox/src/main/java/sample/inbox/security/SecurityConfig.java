package sample.inbox.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Rob Winch
 */
@Configuration
public class SecurityConfig {
	// @formatter:off
	@Bean
	MapReactiveUserDetailsService userDetailsService() {
		UserDetails rob = User.withUsername("rob@example.com")
				.password("{bcrypt}$2a$10$sU1NRy7TqnYiSAcbsO8/3OF3B2I6r4R/5m9YWvr.bE8gKxDo2UfB2")
				.roles("USER")
				.build();
		return new MapReactiveUserDetailsService(rob);
	}
	// @formatter:on
}
