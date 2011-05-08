package de.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.basic.HttpURLConnectionResponseAdapter;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

public class MendeleyOAuthProvider extends AbstractOAuthProvider {

	private static final long serialVersionUID = 1L;

	public MendeleyOAuthProvider(String requestTokenEndpointUrl,
			String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
		super(requestTokenEndpointUrl, accessTokenEndpointUrl,
				authorizationWebsiteUrl);
	}

	protected HttpRequest createRequest(String endpointUrl)
			throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(endpointUrl)
				.openConnection();
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(false);
		connection.setRequestProperty("Content-Length", "0");
		return new HttpURLConnectionRequestAdapter(connection);
	}

	protected HttpResponse sendRequest(HttpRequest request) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) request.unwrap();
		connection.connect();
		System.out.println(connection.getURL());
		return new HttpURLConnectionResponseAdapter(connection);
	}

	protected void closeConnection(HttpRequest request, HttpResponse response) {
		HttpURLConnection connection = (HttpURLConnection) request.unwrap();
		if (connection != null) {
			connection.disconnect();
		}
	}
}
