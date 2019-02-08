package org.pac4j.demo.jee;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
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
        oidcConfiguration.setClientId("167480702619-8e1lo80dnu8bpk3k0lvvj27noin97vu9.apps.googleusercontent.com");
        oidcConfiguration.setSecret("MhMme_Ik6IH2JMnAT6MFIfee");
        //oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        //oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
        oidcConfiguration.addCustomParam("prompt", "consent");
        final GoogleOidcClient oidcClient = new GoogleOidcClient(oidcConfiguration);
       // final OidcClient oidcClient = new OidcClient(oidcConfiguration);
       // oidcClient.setAuthorizationGenerator((ctx, profile) -> { profile.addRole("ROLE_ADMIN"); return profile; });

		/*
		 * final FormClient formClient = new FormClient(
		 * "http://localhost:8080/jee-pac4j-cdi-demo/loginForm.action", new
		 * SimpleTestUsernamePasswordAuthenticator() );
		 * 
		 * final FormClient jsfFormClient = new FormClient(
		 * "http://localhost:8080/jee-pac4j-cdi-demo/jsfLoginForm.action", new
		 * SimpleTestUsernamePasswordAuthenticator() );
		 * jsfFormClient.setName("jsfFormClient");
		 */

        /*final SAML2Configuration cfg = new SAML2Configuration("resource:samlKeystore.jks",
                "pac4j-demo-passwd",
                "pac4j-demo-passwd",
                "resource:testshib-providers.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
        cfg.setServiceProviderMetadataPath(new File("sp-metadata.xml").getAbsolutePath());
        final SAML2Client saml2Client = new SAML2Client(cfg);*/

		/*
		 * final FacebookClient facebookClient = new FacebookClient("145278422258960",
		 * "be21409ba8f39b5dae2a7de525484da8"); facebookClient.setScope(
		 * "user_likes,user_birthday,email,user_hometown,user_location"); final
		 * TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA",
		 * "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs"); // HTTP final
		 * IndirectBasicAuthClient indirectBasicAuthClient = new
		 * IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
		 * 
		 * // CAS final CasConfiguration configuration = new
		 * CasConfiguration("https://casserverpac4j.herokuapp.com/login"); //final
		 * CasConfiguration configuration = new
		 * CasConfiguration("http://localhost:8888/cas/login"); final CasProxyReceptor
		 * casProxy = new CasProxyReceptor();
		 * //configuration.setProxyReceptor(casProxy); final CasClient casClient = new
		 * CasClient(configuration);
		 * 
		 * // REST authent with JWT for a token passed in the url as the token parameter
		 * final List<SignatureConfiguration> signatures = new ArrayList<>();
		 * signatures.add(new SecretSignatureConfiguration(JWT_SALT)); ParameterClient
		 * parameterClient = new ParameterClient("token", new
		 * JwtAuthenticator(signatures)); parameterClient.setSupportGetRequest(true);
		 * parameterClient.setSupportPostRequest(false);
		 * 
		 * // basic auth final DirectBasicAuthClient directBasicAuthClient = new
		 * DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
		 */
        
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
        return config;
    }
}
