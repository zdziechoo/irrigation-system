package pl.ttpsc.irrigation.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser2 {

	public static String getAll(String urlStr) throws IOException {
		
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		try{
				if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			return getJson(rd);	
		}
		finally{
			rd.close();		
			conn.disconnect();
		}
	}	
	
	public static String getJson(BufferedReader br) throws IOException{
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null){
			sb.append(line);
		}
		return sb.toString();
	}
	
	
}
