package de.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class MendeleyAuth {

	private static OAuthConsumer consumer = new DefaultOAuthConsumer(
			OAuthData.CONSUMERKEY, OAuthData.CONSUMERSECRET);
	private static OAuthProvider provider = new MendeleyOAuthProvider(
			OAuthData.REQUESTTOKEN, OAuthData.ACCESSTOKEN, OAuthData.AUTHORIZE);
	private static boolean isAuthenticated = false;
	private static String ACCESSTOKEN;
	private static String ACCESSTOKENSECRET;

	public static void main(String[] args) {
		isAuthenticated = checkProperties();
		if (!isAuthenticated) {
			System.out.println("User is not auth");
			init();
		} else {
			System.out.println("User is auth");
			consumer.setTokenWithSecret(ACCESSTOKEN, ACCESSTOKENSECRET);
		}
		try {
			tryAPICall("http://api.mendeley.com/oapi/documents/search/authors:Wolfgang+Reinhardt/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean checkProperties() {
		System.out.println("Checking properties");
		Reader reader = null;
		try {
			File propertiesFile = new File("properties.txt");
			if (propertiesFile.exists()) {
				reader = new FileReader(propertiesFile);
				Properties prop = new Properties();
				prop.load(reader);
				ACCESSTOKEN = prop.getProperty("ACCESSTOKEN");
				System.out.println("ACCESSTOKEN: " + ACCESSTOKEN);
				ACCESSTOKENSECRET = prop.getProperty("ACCESSTOKENSECRET");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ACCESSTOKEN != null) {
			return true;
		} else
			return false;
	}

	private static void writeProperties(String token, String secret) {
		Writer writer = null;
		try {
			writer = new FileWriter("properties.txt");
			Properties prop = new Properties(System.getProperties());
			prop.setProperty("ACCESSTOKEN", token);
			prop.setProperty("ACCESSTOKENSECRET", secret);
			prop.store(writer, "OAuth Data for Mendeley");
			System.out.println("User Data stored in properties.txt");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void init() {
		consumer = new DefaultOAuthConsumer(OAuthData.CONSUMERKEY,
				OAuthData.CONSUMERSECRET);
		provider = new MendeleyOAuthProvider(OAuthData.REQUESTTOKEN,
				OAuthData.ACCESSTOKEN, OAuthData.AUTHORIZE);
		String requestToken;
		try {
			requestToken = provider.retrieveRequestToken(consumer,
					OAuth.OUT_OF_BAND);
			System.out.println("Enter this URL in your browser: "
					+ requestToken);
			System.out.println("Enter the PIN and confirm with ENTER: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String pin = br.readLine();
			System.out.println("Fetching access token from Mendeley...");
			provider.setOAuth10a(true);
			provider.retrieveAccessToken(consumer, pin);
			// Save the user data
			writeProperties(consumer.getToken(), consumer.getTokenSecret());
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void tryAPICall(String which) throws Exception {
		// Get some API things..
		URL url = new URL(which);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		consumer.sign(request);
		System.out.println("Url: " + request.getURL());
		request.connect();
		System.out.println("Status code: " + request.getResponseCode());
		System.out.println("Message: " + request.getResponseMessage());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				request.getInputStream()));
		String inputLine;
		String answer = "";
		while ((inputLine = in.readLine()) != null)
			answer += inputLine;
		in.close();
		System.out.println(answer);
	}
}
