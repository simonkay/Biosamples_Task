/**
 * 
 */
package uk.ac.ebi.task.biosamples;

import java.io.IOException;
import java.util.List;

/**
 * Java application with main entry point.
 * 
 * @author simonk
 *
 */
public class BioSamplesApp {

	private final BioSamplesRequestHandler requestHandler;

	/**
	 * Canonical constructor creates the request handler.
	 */
	public BioSamplesApp() {
		requestHandler = new BioSamplesRequestHandler();
	}

	/**
	 * Goes through the arguments and performs the correct request or sends an error
	 * message.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public void processArguments(String[] args) throws IOException {

		if (args.length < 1) {
			System.err.println("Please supply at least one argument: 'total', 'accession', 'attributes'.");
			System.exit(1);
		}

		String task = args[0];

		switch (task) {

		case "total":
			int total = requestHandler.getTotalNumberOfSamples();
			System.out.println("The total numnber of samples in Biosamples is currently: " + total);
			break;

		case "accession":
			if (args.length < 2) {
				System.err.println("Please supply an accession.");
				System.exit(1);
			}

			String name = requestHandler.getSampleNameByAccession(args[1]);
			System.out.println("The name of the accession " + args[1] + " is " + name);
			break;

		case "attributes":
			if (args.length < 3) {
				System.err.println("Please supply an attribute and value");
				System.exit(1);
			}

			List<String> accessions = requestHandler.getAccessionsByAttributeAndValue(args[1], args[2]);

			System.out.println("The list of the accessions found for " + args[1] + " and " + args[2] + " is:\n");

			for (String accession : accessions) {
				System.out.println(accession);
			}
			break;

		default:
			System.err.println("Please supply one of these arguments: 'total', 'accession', 'attributes'.");
		}

	}

	/**
	 * Main entry point of application; requires certain arguments, for example:
	 * 'total' 'accession SAMEA4092674' 'attributes Organism Homo+Sapiens'
	 * 'attributes organism+part liver'
	 * 
	 * Note the use of '+'s for spaces. Output is simply written to the output
	 * stream but could be redirected to a file if required.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		BioSamplesApp app = new BioSamplesApp();
		
		try {
			app.processArguments(args);
		} catch (IOException e) {
			System.err.println("IOExeception performing reequest");
			e.printStackTrace();
		}
	}

}
