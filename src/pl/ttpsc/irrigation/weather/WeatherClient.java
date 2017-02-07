package pl.ttpsc.irrigation.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


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
		weather.setIcon(lastestWeatherStatus.getString("icon"));
		
		//String iconInfo = lastestWeatherStatus.getString("icon");
		weather.setRain(isRain(weather.getIcon()));

		return weather;
	}

	private static boolean isRain(String iconInfo) {
		if (
				!iconInfo.equals("09d") &&!iconInfo.equals("09n")
				&& !iconInfo.equals("10d") && !iconInfo.equals("10n") 
				&&!iconInfo.equals("11d") &&!iconInfo.equals("11n")
				&& !iconInfo.equals("13d")&& !iconInfo.equals("13n")
				&& !iconInfo.equals("50d")&& !iconInfo.equals("50n")
				) {
			return false;
		} else
			return true;
	}

}
