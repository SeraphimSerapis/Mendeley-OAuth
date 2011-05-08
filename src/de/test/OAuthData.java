package de.test;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;

public class OAuthData {
	// Application Data
	public static final String CONSUMERKEY = "PLACEKEYHERE";
	public static final String CONSUMERSECRET = "PLACESECRETHERE";
	// Mendeley OAuth urls
	public static final String BASEURL = "http://www.mendeley.com";
	public static final String REQUESTTOKEN = "http://www.mendeley.com/oauth/request_token/";
	public static final String ACCESSTOKEN = "http://www.mendeley.com/oauth/access_token/";
	public static final String AUTHORIZE = "http://www.mendeley.com/oauth/authorize/";
	/**
	 * Our basic OAuth components
	 */
	private static OAuthConsumer consumer = null;
	private static OAuthProvider provider = null;

	public static OAuthConsumer getConsumer() {
		if (consumer == null) {
			consumer = new DefaultOAuthConsumer(OAuthData.CONSUMERKEY,
					OAuthData.CONSUMERSECRET);
		}
		return consumer;
	}

	public static OAuthProvider getProvider() {
		if (provider == null) {
			provider = new MendeleyOAuthProvider(OAuthData.REQUESTTOKEN,
					OAuthData.ACCESSTOKEN, OAuthData.AUTHORIZE);
		}
		return provider;
	}
}
