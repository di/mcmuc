package edu.drexel.cs544.mcmuc.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Certificate represents a public-key certificate that can be used for private 
 * communication between clients. A Certificate has a format (e.g., X.509, PGP)
 * and the certificate itself, which is typically a collection of hierarchical
 * key/value pairs in a particular notation, such as ASN.1. However, for the purposes
 * of the protocol, each are simply stored as Strings that each may use as they see fit.
 *
 * The JSON format of a Certificate is {'format':'<format>','certificate':'<certificate>'}
 */
public class Certificate implements JSON {
	private String format;
	private String certificate;
	
	/**
	 * Accessor for certificate's format
	 * @return String format
	 */
	public String getFormat()
	{
		return format;
	}
	
	/**
	 * Accessor for certificiate
	 * @return String certificate
	 */
	public String getCertificate()
	{
		return certificate;
	}
	
	/**
	 * A Certificate is the combination of a public-key certificate and the format it is
	 * represented in
	 * @param format type of certificate, such as X.509 or PGP
	 * @param certificate the actual certificate
	 */
	public Certificate(String format, String certificate)
	{
		this.format = format;
		this.certificate = certificate;
	}
	
	/**
	 * Deserializes the JSON into the Certificate object - an error occurs if the JSON format
	 * is incorrect or required key/value pairs (format, certificate) are missing.
	 * @param json JSON representation of a Certificate
	 */
	public Certificate(JSONObject json)
	{
		try {
			format = json.getString("format");
			certificate = json.getString("certificate");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serializes the Certificate object into JSON notation
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("format", format);
			json.put("certificate", certificate);
		} catch (JSONException e) {

		}
		
		return json;
	}
}