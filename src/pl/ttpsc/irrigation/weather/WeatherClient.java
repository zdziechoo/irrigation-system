package pl.ttpsc.irrigation.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherClient {

	private static final String URL = "http://api.openweathermap.org/data/2.5/weather?&units=metric";
	private static final String APPID = "&APPID=cbc91cb05d4bc8fa06dcb13a9ef5a82b";

	public WeatherClient() {
	}

	public static Weather getWeather(double longitude, double latitude) throws IOException, JSONException {

		Weather weather = new Weather();
		
		StringBuilder sb = new StringBuilder(URL);
		sb.append("&lon=").append(longitude);
		sb.append("&lat=").append(latitude);
		sb.append(APPID);
				
		String weatherInfo = JsonParser.getAll(sb.toString());
		JSONObject obj = new JSONObject(weatherInfo);

		JSONObject main = (JSONObject) obj.getJSONObject("main");

		weather.setTemperature(main.getDouble("temp"));
		weather.setPressure(main.getDouble("pressure"));
		weather.setHumidity(main.getDouble("humidity"));

		JSONArray weatherStatusArray = (JSONArray) obj.getJSONArray("weather");
		JSONObject lastestWeatherStatus = (JSONObject) weatherStatusArray.get(0);
		String iconInfo = lastestWeatherStatus.getString("icon");
		weather.setRain(isRain(iconInfo));

		return weather;
	}

	private static boolean isRain(String iconInfo) {
		if (!iconInfo.equals("09d") && !iconInfo.equals("10d") && !iconInfo.equals("11d") && !iconInfo.equals("13d")) {
			return false;
		} else
			return true;
	}

}
