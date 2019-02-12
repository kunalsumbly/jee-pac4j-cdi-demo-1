package org.pac4j.oidc.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;

@SuppressWarnings("rawtypes")
public class CustomOidcClient extends OidcClient<OidcConfiguration> {

	public CustomOidcClient() {
	}

	public CustomOidcClient(final OidcConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected void clientInit() {
		CommonHelper.assertNotNull("configuration", getConfiguration());
		getConfiguration().setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");

		// oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
		// final GoogleOidcClient oidcClient = new GoogleOidcClient(oidcConfiguration);
		final OidcProfileCreator profileCreator = new OidcProfileCreator(getConfiguration());
        profileCreator.setProfileDefinition(new OidcProfileDefinition<>());
        defaultProfileCreator(profileCreator);
        
        defaultLogoutActionBuilder(new LogoutActionBuilder() {

			@Override
			public RedirectionAction getLogoutAction(WebContext context, UserProfile currentProfile, String targetUrl) {
				final String redirectUrl = "https://accounts.google.com/Logout";
				logger.debug("redirectUrl: {}", redirectUrl);
				return new FoundAction(redirectUrl);
			}
		});
		
		super.clientInit();
	}
}
