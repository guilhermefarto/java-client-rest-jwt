package farto.cleva.guilherme;

import java.text.MessageFormat;
import java.util.Iterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import farto.cleva.guilherme.utils.JSONUtil;

public class Main {

	private static final boolean DEBUG = true;

	public static final String BASE_URL = "http://localhost:3000/";

	public static final String TOKEN_URL = BASE_URL + "api/v1/oauth/token";
	public static final String DATA_URL = BASE_URL + "api/v1/data";

	private static final String TOKEN_REQUEST_JSON = "{ \"email\": \"guilherme.farto@gmail.com\", \"password\": \"pwd@123\" }";

	private static final String BEARER_AUTHORIZATION = "Bearer {0}";

	private static final String APPLICATION_JSON = "application/json";

	private static final String HTTP_RESPONSE_LOG = "HTTP Status: {0} for [{1}]";

	private static final String RECORDS_LOG = "{0} records retrieved for [{1}]";
	private static final String NO_RECORDS_LOG = "No records retrieved for [{0}]";

	private static final String EMPTY = "";

	/**
	 * Example of an implementation that performs REST requests to fetch
	 * authorization token (WebService based on JWT) and extracts JSON data
	 * using Authorization header
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		HttpClient client = null;
		HttpPost post = null;
		HttpGet get = null;
		HttpResponse response = null;

		try {
			client = HttpClientBuilder.create().build();

			post = new HttpPost(TOKEN_URL);

			post.setEntity(new StringEntity(TOKEN_REQUEST_JSON));
			post.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
			post.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 200
				System.out.println(MessageFormat.format(HTTP_RESPONSE_LOG, HttpStatus.SC_OK, TOKEN_URL));

				JSONObject tokenResponseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(response.getEntity()));

				String token = MessageFormat.format(BEARER_AUTHORIZATION, String.valueOf(tokenResponseJson.get("token")));

				System.out.println(token);
				System.out.println(EMPTY);

				get = new HttpGet(DATA_URL);

				get.setHeader(HttpHeaders.AUTHORIZATION, token);
				post.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
				get.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

				response = client.execute(get);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 200
					System.out.println(MessageFormat.format(HTTP_RESPONSE_LOG, HttpStatus.SC_OK, DATA_URL));

					String originalJson = EntityUtils.toString(response.getEntity());

					if (DEBUG) {
						System.out.println(EMPTY);
						System.out.println("JSON response data");
						System.out.println(originalJson);
						System.out.println(EMPTY);
					}

					JSONArray dataResponseJson = (JSONArray) new JSONParser().parse(originalJson);

					if (dataResponseJson != null) {
						System.out.println(MessageFormat.format(RECORDS_LOG, dataResponseJson.size(), DATA_URL));
						System.out.println(EMPTY);

						Iterator<?> iDataResponseJson = dataResponseJson.iterator();

						while (iDataResponseJson.hasNext()) {
							JSONObject itemResponseJson = (JSONObject) iDataResponseJson.next();

							if (DEBUG) {
								JSONUtil.prettyPrint(itemResponseJson);
							} else { // retrieving the attributes (properties) from the JSON response data
								JSONObject itemHeaderResponseJson = (JSONObject) itemResponseJson.get("header");
								JSONArray itemRowsResponseJson = (JSONArray) itemResponseJson.get("rows");

								System.out.println("ID: " + itemHeaderResponseJson.get("id"));
								System.out.println("Date: " + itemHeaderResponseJson.get("date"));
								System.out.println("Revision: " + itemHeaderResponseJson.get("revision"));
								System.out.println(EMPTY);

								for (Iterator<?> iItemRowsResponseJson = itemRowsResponseJson.iterator(); iItemRowsResponseJson.hasNext();) {
									JSONObject nestedItemResponseJson = (JSONObject) iItemRowsResponseJson.next();

									System.out.println("  Operation: " + nestedItemResponseJson.get("operation"));
									System.out.println("  Hours: " + nestedItemResponseJson.get("hours"));
									System.out.println("  Area: " + nestedItemResponseJson.get("area"));
									System.out.println(EMPTY);
								}
							}
						}
					} else {
						System.out.println(MessageFormat.format(NO_RECORDS_LOG, DATA_URL));
					}
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) { // 401
					System.out.println(MessageFormat.format(HTTP_RESPONSE_LOG, HttpStatus.SC_UNAUTHORIZED, DATA_URL));
				}
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) { // 401
				System.out.println(MessageFormat.format(HTTP_RESPONSE_LOG, HttpStatus.SC_UNAUTHORIZED, TOKEN_URL));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
