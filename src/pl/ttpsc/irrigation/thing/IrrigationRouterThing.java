package pl.ttpsc.irrigation.thing;

import java.io.IOException;

import org.json.JSONException;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.LocationPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.primitives.structs.Location;

import pl.ttpsc.irrigation.weather.Weather;
import pl.ttpsc.irrigation.weather.WeatherClient;

@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
		@ThingworxPropertyDefinition(name = "RouterLocation", description = "Router Location", baseType = "LOCATION", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }), 
		@ThingworxPropertyDefinition(name = "Temperature", description = "Weather: temperature", baseType = "NUMBER", category = "Status", aspects = {
		"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "Humidity", description = "Weather: humidity", baseType = "NUMBER", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "AtmosphericPressure", description = "Weather: atmospheric pressure", baseType = "NUMBER", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "IsRain", description = "Weather: boolean type to define raining on the area", baseType = "BOOLEAN", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "Icon", description = "Weather: boolean type to define raining on the area", baseType = "STRING", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" })})

public class IrrigationRouterThing extends VirtualThing {

	private final static String ROUTER_LOCATION = "RouterLocation";
	private final static String TEMPERATURE = "Temperature";
	private final static String HUMIDITY = "Humidity";
	private final static String ATMOSPHERIC_PRESSURE = "AtmosphericPressure";
	private final static String IS_RAIN = "IsRain";
	private final static String ICON = "Icon";

	private Location routerLocation;
	//private Double temperature;
	
	private Weather weather;

	public IrrigationRouterThing(String name, String description, ConnectedThingClient client) throws IOException, JSONException {
		super(name, description, client);
		super.initializeFromAnnotations();
		this.init();
	}

	private void init() throws IOException, JSONException {
		// Get the current values from the ThingWorx composer
		routerLocation = getRouterLocation();
	}

	@Override
	public void processScanRequest() throws Exception {
		super.processScanRequest();
		this.setLocationLodz();
		this.setWeather();
		this.setTemperature();
		this.setHumidity();
		this.setAtmosphericPressure();
		this.setIsRain();
		this.setIcon();
		this.updateSubscribedProperties(150000);

	}

	private void setLocationLodz() throws Exception {
		this.routerLocation = new Location(19.4623015d, 51.7462748d);
		LocationPrimitive loc = new LocationPrimitive(routerLocation);
		super.setProperty(ROUTER_LOCATION, loc);
	}

	public Location getRouterLocation() {
		return (Location) getProperty(ROUTER_LOCATION).getValue().getValue();
	}
	
	public void setWeather() throws IOException, JSONException{
		this.weather = WeatherClient.getWeather(routerLocation.getLongitude(), routerLocation.getLatitude());
	}
	
	public void setRouterLocation() throws Exception {
		setProperty(ROUTER_LOCATION, new LocationPrimitive(this.routerLocation));
	}
	
	public Double getTemperature() {
		return (Double) getProperty(TEMPERATURE).getValue().getValue();
	}

	public void setTemperature() throws Exception {
		setProperty(TEMPERATURE, new NumberPrimitive(this.weather.getTemperature()));
	}
	
	public Double getHumidity() {
		return (Double) getProperty(HUMIDITY).getValue().getValue();
	}

	public void setHumidity() throws Exception {
		setProperty(HUMIDITY, new NumberPrimitive(this.weather.getHumidity()));
	}
	
	public Double getAtmosphericPressure() {
		return (Double) getProperty(ATMOSPHERIC_PRESSURE).getValue().getValue();
	}

	public void setAtmosphericPressure() throws Exception {
		setProperty(ATMOSPHERIC_PRESSURE, new NumberPrimitive(this.weather.getPressure()));
	}
	
	public Boolean isRain() {
		return (Boolean) getProperty(IS_RAIN).getValue().getValue();
	}

	public void setIsRain() throws Exception {
		setProperty(IS_RAIN, new BooleanPrimitive(this.weather.isRain()));
	}
	
	public String getIcon() {
		return (String) getProperty(ICON).getValue().getValue();
	}

	public void setIcon() throws Exception {
		setProperty(ICON, new StringPrimitive(this.weather.getIcon()));
	}

	public void synchronizeState() {
		super.synchronizeState();
		super.syncProperties();
	}

	@Override
	public void processPropertyWrite(PropertyDefinition property, @SuppressWarnings("rawtypes") IPrimitiveType value) throws Exception {
		this.setPropertyValue(property.getName(), value);
	}

}
