package org.pac4j.demo.jee;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.client.CustomOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pac4J configuration used for demonstration and experimentation.
 *
 * @author Phillip Ross
 */
@Dependent
public class SecurityConfig {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public static final String JWT_SALT = "12345678901234567890123456789012";

    /**
     * Build the Pac4J-specific configuration.
     *
     * @return a Pac4J config containing clients, authorizers, etc
     */
    @Produces @ApplicationScoped
    private Config buildConfiguration() {
        logger.debug("building Security configuration...");
        System.out.println("building Security configuration...");
        // Google OIDC configuration/client
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        //oidcConfiguration.setClientId("167480702619-8e1lo80dnu8bpk3k0lvvj27noin97vu9.apps.googleusercontent.com");
        //oidcConfiguration.setSecret("MhMme_Ik6IH2JMnAT6MFIfee");
        oidcConfiguration.setClientId("980910461423-qvogk679mh5mm86teo4chjk8rqurut53.apps.googleusercontent.com");
        oidcConfiguration.setSecret("hPMqqMVbjhU5G8jsDWPKcGSH");
        oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        
        oidcConfiguration.addCustomParam("prompt", "consent");
       //final GoogleOidcClient oidcClient = new GoogleOidcClient(oidcConfiguration);
      // create the client 
       final OidcClient oidcClient = new OidcClient(oidcConfiguration);
       oidcClient.setLogoutActionBuilder(new LogoutActionBuilder() {
		@Override
		public RedirectionAction getLogoutAction(WebContext context, UserProfile currentProfile, String targetUrl) {
			 final String redirectUrl = "https://accounts.google.com/Logout";
		        logger.debug("redirectUrl: {}", redirectUrl);
		        return new FoundAction(redirectUrl);
		}
	});
      	
       // register the client        
        final Clients clients = new Clients(
                "http://localhost:8080/callback",
                oidcClient               
        );

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.addAuthorizer("mustBeAnon", new IsAnonymousAuthorizer<>("/?mustBeAnon"));
        config.addAuthorizer("mustBeAuth", new IsAuthenticatedAuthorizer<>("/?mustBeAuth"));
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.action$"));
        //config.
        return config;
    }
}
