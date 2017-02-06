package pl.ttpsc.irrigation.weather;

import java.io.IOException;

import org.json.JSONException;

import com.thingworx.types.primitives.structs.Location;

public class Test {
	public static void main(String[] args) throws IOException, JSONException {
		Weather weather;
		///WeatherClient weatherClient = new WeatherClient();
				
		weather = WeatherClient.getWeather(19.12,18.12);
		System.out.println(weather.isRain());
		System.out.println(weather.getTemperature());
		System.out.println(weather.getHumidity());
		System.out.println(weather.getPressure());		
	}
}
