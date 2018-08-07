/**
 * 
 */
package uk.ac.ebi.task.biosamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * 
 * @author simonk
 *
 */
public class BioSamplesRequestHandler {

	private static final String SAMPLES_URL = "https://www.ebi.ac.uk/biosamples/samples/";
	private static final String CONTENT_TYPE = "application/hal+json;charset=UTF-8";

	/**
	 * All HTTP requests are made via this client.
	 */
	private CloseableHttpClient httpClient;

	/**
	 * Issues an HTTP GET request with the query appended to the Biosample URL
	 * setting the appropraite content type and converting the response into a JSON
	 * object.
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	private JSONObject getRequestObject(String query) throws IOException {

		httpClient = HttpClients.createDefault();

		HttpGet getRequest = new HttpGet(SAMPLES_URL + query);
		getRequest.setHeader("Content-type", CONTENT_TYPE);

		HttpResponse response = httpClient.execute(getRequest);

		int responsecode = response.getStatusLine().getStatusCode();

		switch (responsecode) {

		case HttpStatus.SC_OK:
			// just continue
			break;

		case HttpStatus.SC_UNAUTHORIZED:
			System.err.println("AUTHENTICATION_ERROR");

		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			System.err.println("SYSTEM_ERROR_INTERNAL");

		case HttpStatus.SC_BAD_REQUEST:
			System.err.println("SYSTEM_ERROR_BAD_REQUEST: " + getRequest);

		case HttpStatus.SC_SERVICE_UNAVAILABLE:
			System.err.println("SYSTEM_ERROR_UNAVAILABLE");

		default:
			System.err.println("SYSTEM_ERROR_OTHER: " + getRequest);

			// In all other cases exit
			httpClient.close();
			System.exit(1);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder responseString = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			responseString.append(line);
		}

		httpClient.close();

		JSONObject json = new JSONObject(responseString.toString());

		return json;
	}

	/**
	 * Takes an accession and retrieve the name of the returned sample.
	 * 
	 * @param accession
	 * @return
	 * @throws IOException
	 */
	public String getSampleNameByAccession(String accession) throws IOException {

		JSONObject json = getRequestObject(accession);

		String name = json.get("name").toString();

		return name;
	}

	/**
	 * Queries the default URL and returns the totalElements from the "page"
	 * section.
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getTotalNumberOfSamples() throws IOException {

		JSONObject json = getRequestObject("");

		JSONObject page = (JSONObject) json.get("page");

		String elements = page.get("totalElements").toString();

		return Integer.parseInt(elements);
	}

	/**
	 * Uses the "filter" parameter with the given attribute and value and extracts a
	 * list of accessions. Note that only the first 5000 results are returned.
	 * 
	 * @param attribute
	 * @param value
	 * @throws IOException
	 */
	public List<String> getAccessionsByAttributeAndValue(String attribute, String value) throws IOException {

		String filter = "?filter=attr:" + attribute + ":" + value + "&size=5000";

		JSONObject json = getRequestObject(filter);

		JSONArray samples = (JSONArray) ((JSONObject) json.get("_embedded")).get("samples");

		if (samples.length() == 5000) {
			System.out.println("Only returning the first 5000 results from your query");
		}

		List<String> accessions = new LinkedList<String>();

		for (int index = 0; index < samples.length(); index++) {

			JSONObject sample = samples.getJSONObject(index);

			String accession = sample.get("accession").toString();

			accessions.add(accession);
		}

		return accessions;
	}

}
