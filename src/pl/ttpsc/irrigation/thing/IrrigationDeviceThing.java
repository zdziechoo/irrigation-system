package pl.ttpsc.irrigation.thing;

import java.io.IOException;
import java.util.Random;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.types.constants.CommonPropertyNames;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.LocationPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.primitives.structs.Location;

import pl.ttpsc.irrigation.weather.Weather;
import pl.ttpsc.irrigation.weather.WeatherClient;

@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
		@ThingworxPropertyDefinition(name = "WaterPressure", description = "Water pressure in the pump", baseType = "NUMBER", category = "Status", aspects = {
				"isReadOnly:FALSE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "IrrigationStrength", description = "Irrigation Strength", baseType = "NUMBER", category = "Status", aspects = {
				"isReadOnly:FALSE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "Location", description = "Location", baseType = "LOCATION", category = "Status", aspects = {
				"isReadOnly:FALSE", "pushType:VALUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "IrrigationState", description = "Irrigation State", baseType = "BOOLEAN", category = "Status", aspects = {
				"isReadOnly:FALSE", "defaultValue:TRUE", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "AlarmState", description = "Alarm State", baseType = "INTEGER", category = "Status", aspects = {
				"isReadOnly:FALSE", "isLogged:TRUE", "defaultValue:3", "isPersistent:TRUE" }),
		@ThingworxPropertyDefinition(name = "RouterName", description = "Router Name", baseType = "THINGNAME", category = "Status", aspects = {
				"isReadOnly:FALSE", "isPersistent:TRUE" }), })

public class IrrigationDeviceThing extends VirtualThing {

	private static final Logger LOG = LoggerFactory.getLogger(IrrigationDeviceThing.class);

	private final static String WATER_PRESSURE = "WaterPressure";
	private final static String IRRIGATION_STRENGTH = "IrrigationStrength";
	private final static String LOCATION = "Location";
	private final static String IRRIGATION_STATE = "IrrigationState";
	private final static String ALARM_STATE = "AlarmState";
	private final static String ROUTER_NAME = "RouterName";

	Double waterPressure;
	Double irrigationStrength;
	Location location;
	boolean irrigationState;
	Integer alarmState;
	String routerName;

	private Weather weather;

	public IrrigationDeviceThing(String name, String description, ConnectedThingClient client) throws IOException, JSONException {
		super(name, description, client);
		super.initializeFromAnnotations();
		this.init();
	}

	private void init() throws IOException, JSONException {
		// Get the current values from the ThingWorx composer
		waterPressure = getWaterPressure();
		irrigationStrength = getIrrigationStrength();
		location = getLocation();
		irrigationState = isIrrigationState();
		alarmState = getAlarmState();
		routerName = getRouterName();
		weather = getWeather(location.getLongitude(), location.getLatitude());
	}

	@Override
	public void processScanRequest() throws Exception {

		super.processScanRequest();

		/*this.setSimpleWaterPressure();
		this.setSimpleIrrigationStrength();*/
		/*this.setOffIrrigationIfRain();*/
		irrigationState = isIrrigationState();
		this.setOffIrraginationIfAlarmStateIs();
		this.setOffIrraginationIfIrrigationStateOff();
		// this.setSimpleLocation();
		// this.setSimpleIrrigationState();
		/* this.setSimpleAlarmState(); */

		this.updateSubscribedProperties(15000);

	}
	
	private void setSimpleWaterPressure() throws Exception {
		if (!isAlarmStateOnDevice()) {
			this.waterPressure = 50 + Math.random() * 50;
			setWaterPressure();	
		} else {
			this.waterPressure = 0.0;
			setWaterPressure();
		}
	}
	
	private void setSimpleIrrigationStrength() throws Exception {
		if (!isAlarmStateOnDevice()){
			this.irrigationStrength = Math.random() * 100;
			setIrrigationStrength();
			
		} else {
			this.irrigationStrength = 0.0;
			setIrrigationStrength();
		}
	}

	private void setSimpleAlarmState() throws Exception {
		Random generator = new Random();
		this.alarmState = generator.nextInt(4);
		super.setProperty(ALARM_STATE, alarmState);
	}

	@ThingworxServiceDefinition(name = "SwitchOnIrrigation", description = "Switch on irrigation")
	@ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result", baseType = "NOTHING")
	public void SwitchOnIrrigation() throws Exception {
		this.irrigationState = true;
		this.setIrrigationState();
	}

	@ThingworxServiceDefinition(name = "SwitchOffIrrigation", description = "Switch off irrigation")
	@ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result", baseType = "NOTHING")
	public void SwitchOffIrrigation() throws Exception {
		this.irrigationState = false;
		this.setIrrigationState();
	}

	@ThingworxServiceDefinition(name = "SetStrengthOfIrrigation", description = "Set irrigation strength of water")
	@ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result", baseType = "NOTHING")
	public void SetStrengthOfIrrigation(
			@ThingworxServiceParameter(name = "irrigationStrength", description = "Strength of irrigation", baseType = "NUMBER") Double irrigationStrength)
			throws Exception {
		LOG.info("Setting irrigation strenght in device as {}", irrigationStrength);
		this.irrigationStrength = irrigationStrength;
		this.setIrrigationStrength();
	}

	@ThingworxServiceDefinition(name = "ResetLastAlarm", description = "Reset last alarm state")
	@ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result", baseType = "NOTHING")
	public void ResetLastAlarm() throws Exception {
		if (isAlarmStateOnDevice()) {
			LOG.info("Resetting alarm state on device");
			this.alarmState = 3;
			this.setAlarmState();
		}
	}

	public Double getWaterPressure() {
		return (Double) getProperty(WATER_PRESSURE).getValue().getValue();
	}

	public void setWaterPressure() throws Exception {
		setProperty(WATER_PRESSURE, new NumberPrimitive(this.waterPressure));
	}

	public Double getIrrigationStrength() {
		return (Double) getProperty(IRRIGATION_STRENGTH).getValue().getValue();
	}

	public void setIrrigationStrength() throws Exception {
		setProperty(IRRIGATION_STRENGTH, new NumberPrimitive(this.irrigationStrength));
	}

	public Location getLocation() {
		return (Location) getProperty(LOCATION).getValue().getValue();
	}

	public void setLocation() throws Exception {
		setProperty(LOCATION, new LocationPrimitive(this.location));
	}

	public boolean isIrrigationState() {
		return (Boolean) getProperty(IRRIGATION_STATE).getValue().getValue();
	}

	public void setIrrigationState() throws Exception {
		setProperty(IRRIGATION_STATE, new BooleanPrimitive(this.irrigationState));
	}

	public Integer getAlarmState() {
		return (Integer) getProperty(ALARM_STATE).getValue().getValue();
	}

	public void setAlarmState() throws Exception {
		setProperty(ALARM_STATE, new IntegerPrimitive(this.alarmState));
	}

	public String getRouterName() {
		return (String) getProperty(ROUTER_NAME).getValue().getValue();
	}

	public void setRouterName() throws Exception {
		setProperty(ROUTER_NAME, new StringPrimitive(this.routerName));
	}
	
	private Weather getWeather (double longitude, double latitude) throws IOException, JSONException{
		return WeatherClient.getWeather(location.getLongitude(), location.getLatitude());
	}

	private boolean isAlarmStateOnDevice() {
		this.alarmState = getAlarmState();
		if (this.alarmState != 3) {
			return true;
		} else {
			return false;
		}
	}

	private void setOffIrrigationIfRain() throws Exception {
		if (weather.isRain()) {
			switchOffDevice();
		}
	}

	private void setOffIrraginationIfAlarmStateIs() throws Exception {
		if (isAlarmStateOnDevice()) {
			switchOffDevice();
		}
	}
	
	private void setOffIrraginationIfIrrigationStateOff() throws Exception {
		this.irrigationState = isIrrigationState();
		if(!this.irrigationState){
			this.waterPressure = 0.0;
			this.irrigationStrength = 0.0;
			setWaterPressure();
			setIrrigationStrength();
		}	
	}
	
	private void switchOffDevice() throws Exception{
		this.irrigationState = false;
		this.waterPressure = 0.0;
		this.irrigationStrength = 0.0;
		setIrrigationState();
		setWaterPressure();
		setIrrigationStrength();
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
