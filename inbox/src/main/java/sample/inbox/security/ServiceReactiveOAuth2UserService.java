package sample.inbox.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sample.inbox.user.User;
import sample.inbox.user.UserService;

import java.util.Collection;
import java.util.Map;

/**
 * @author Rob Winch
 */
@Component
public class ServiceReactiveOAuth2UserService
		implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {
	private ReactiveOAuth2UserService<OidcUserRequest, OidcUser> delegate = new OidcReactiveOAuth2UserService();

	private final UserService users;

	public ServiceReactiveOAuth2UserService(UserService users) {
		this.users = users;
	}

	@Override
	public Mono<OidcUser> loadUser(OidcUserRequest userRequest)
			throws OAuth2AuthenticationException {
		return this.delegate.loadUser(userRequest)
				.flatMap(this::create);
	}

	private Mono<OidcUser> create(OidcUser user) {
		return this.users.findById((String) user.getAttributes().get("user_id"))
			.switchIfEmpty(Mono.error(() -> new IllegalStateException("No user for alias " + user.getName())))
			.map(u -> new CustomOidcUser(u, user));
	}

	static class CustomOidcUser extends User implements OidcUser {
		private final OidcUser oidcUser;

		CustomOidcUser(User user, OidcUser oidcUser) {
			super(user);
			this.oidcUser = oidcUser;
		}

		@Override
		public Map<String, Object> getClaims() {
			return oidcUser.getClaims();
		}

		@Override
		public OidcUserInfo getUserInfo() {
			return oidcUser.getUserInfo();
		}

		@Override
		public OidcIdToken getIdToken() {
			return oidcUser.getIdToken();
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return oidcUser.getAuthorities();
		}

		@Override
		public Map<String, Object> getAttributes() {
			return oidcUser.getAttributes();
		}

		@Override
		public String getName() {
			return oidcUser.getName();
		}


	}
}
