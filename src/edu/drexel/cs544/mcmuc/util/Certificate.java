package edu.drexel.cs544.mcmuc.util;

import java.util.Arrays;

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
	private byte[] certificate;
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(certificate);
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Certificate))
			return false;
		Certificate other = (Certificate) obj;
		if (!Arrays.equals(certificate, other.certificate))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		return true;
	}

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
	 * @return byte[] certificate
	 */
	public byte[] getCertificate()
	{
		return certificate;
	}
	
	/**
	 * A Certificate is the combination of a public-key certificate and the format it is
	 * represented in
	 * @param format type of certificate, such as X.509 or PGP
	 * @param certificate the actual certificate
	 */
	public Certificate(String format, byte[] certificate)
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
			certificate = stringToBytes(json.getString("certificate"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a byte array into a hex string
	 * @param array byte[] to convert to hex string
	 * @return String of byte[] as hex
	 */
    public static String bytesToString(byte[] array) {
        StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
		    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
    }
    
    /**
     * Converts a hex string to a byte array
     * @param s String of hex to convert to byte[]
     * @return byte[] of hex String
     */
    public static byte[] stringToBytes(String s)
    {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

	/**
	 * Serializes the Certificate object into JSON notation
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("format", format);
			json.put("certificate", bytesToString(certificate));
		} catch (JSONException e) {

		}
		
		return json;
	}
}