package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class Certificate implements JSON {
	private String format;
	private String certificate;
	
	public Certificate(String format, String certificate)
	{
		this.format = format;
		this.certificate = certificate;
	}
	
	public Certificate(JSONObject json)
	{
		try {
			format = json.getString("format");
			certificate = json.getString("certificate");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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