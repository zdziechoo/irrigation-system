package pl.ttpsc.irrigation.thing;

import java.util.Random;

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
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.LocationPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.structs.Location;



@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {	
		@ThingworxPropertyDefinition(name="WaterPressure", description="Water Pressure", baseType="NUMBER", category = "Status", aspects={"isReadOnly:true"}),
		@ThingworxPropertyDefinition(name="IrrigationStrength", description="Irrigation Strength", baseType="NUMBER",category = "Status", aspects={"isReadOnly:true"}),
		@ThingworxPropertyDefinition(name="Location", description="Location", baseType="LOCATION",category = "Status", aspects={"isReadOnly:true"}),
		@ThingworxPropertyDefinition(name="IrrigationState", description = "Irrigation State", baseType = "BOOLEAN",category = "Status", aspects={"isReadOnly:true"}),
		@ThingworxPropertyDefinition(name="AlarmState", description = "Alarm State", baseType = "INTEGER",category = "Status", aspects={"isReadOnly:true"}),
})

public class IrrigationDeviceThing extends VirtualThing{
	
	private static final Logger LOG = LoggerFactory.getLogger(IrrigationDeviceThing.class);

	private final static String WATER_PRESSURE = "WaterPressure";
	private final static String IRRIGATION_STRENGTH = "IrrigationStrength";
	private final static String LOCATION = "Location";
	private final static String IRRIGATION_STATE = "IrrigationState";
	private final static String ALARM_STATE = "AlarmState";
	private String thingName = null;
	
	private Double waterPressure;
	private Double irrigationStrength;
	private Location location;
	private boolean irrigationState;
	private Integer alarmState;
	
	
	public IrrigationDeviceThing(String name, String description, ConnectedThingClient client) {
		super(name, description, client);
		thingName = name;
		super.initializeFromAnnotations();
	}
	
	@Override
	public void processScanRequest() throws Exception {
		
		super.processScanRequest();
		this.setSimpleWaterPressure();
		this.setSimpleIrrigationStrength();
		this.setSimpleLocation();
		this.setSimpleIrrigationState();
		this.setSimpleAlarmState();
		this.updateSubscribedProperties(15000);

	}
	
	private void setSimpleWaterPressure() throws Exception{
		this.waterPressure = 50+Math.random()*50;
		super.setProperty(WATER_PRESSURE, waterPressure);	
	}
	
	private void setSimpleIrrigationStrength () throws Exception{
		this.irrigationStrength = Math.random()*100;
		super.setProperty(IRRIGATION_STRENGTH, irrigationStrength);
	}
	
	private void setSimpleLocation() throws Exception{
		this.location = new Location(51.8447819d, 19.8648268d, 14d);
		LocationPrimitive loc = new LocationPrimitive(location);
		super.setProperty(LOCATION, loc);
	}
	
	private void setSimpleIrrigationState() throws Exception{
		this.irrigationState = false;
		super.setProperty(IRRIGATION_STATE, irrigationState);
	}
	
	private void setSimpleAlarmState() throws Exception{
		Random generator = new Random();
		this.alarmState = generator.nextInt(3);
		super.setProperty(ALARM_STATE, alarmState);
	}
	
	
	/*@ThingworxServiceDefinition(name="SwitchingIrrigationOnOff", description="Service to remotely switch on and off irrigation system")
	@ThingworxServiceResult(name=CommonPropertyNames.PROP_RESULT, description="Result", baseType="NOTHING")
	public void SwitchOnOff(
			@ThingworxServiceParameter(name="irrigationState", description="StateOfDevice", baseType="BOOLEAN") Boolean irrigationState) throws Exception{
				LOG.info("Setting Irrigation as {}", irrigationState);
				this.irrigationState = irrigationState;
				super.setProperty(IRRIGATION_STATE, irrigationState);
			}*/

	public Double getWaterPressure() {
		return (Double) getProperty(WATER_PRESSURE).getValue().getValue();
	}

	public void setWaterPressure() throws Exception {
		setProperty(WATER_PRESSURE, new NumberPrimitive(this.waterPressure));
	}

	public Double getIrrigationStrength() {
		return (Double) getProperty(IRRIGATION_STRENGTH).getValue().getValue();
	}

	public void setIrrigationStrength(Double irrigationStrength) throws Exception {
		 setProperty(IRRIGATION_STRENGTH, this.irrigationStrength);
	}

	public Location getLocation() {
		return (Location) getProperty(LOCATION).getValue().getValue();
	}

	public void setLocation(Location location) throws Exception {
		 setProperty(LOCATION, this.location);
	}

	public boolean isIrrigationState() {
		return (Boolean) getProperty(IRRIGATION_STATE).getValue().getValue();
	}

	public void setIrrigationState(boolean irrigationState) throws Exception {
		setProperty(IRRIGATION_STATE, this.irrigationState);
	}

	public Integer getAlarmState() {
		return (Integer)getProperty(ALARM_STATE).getValue().getValue();
	}

	public void setAlarmState(Integer alarmState) throws Exception {
		 setProperty(ALARM_STATE, this.alarmState);
	}
	
	
	// From the VirtualThing class
	// This method will get called when a connect or reconnect happens
	// Need to send the values when this happens
	// This is more important for a solution that does not send its properties on a regular basis
	public void synchronizeState() {
		// Be sure to call the base class
		super.synchronizeState();
		// Send the property values to ThingWorx when a synchronization is required
		super.syncProperties();
	}
	
	
	@Override
	public void processPropertyWrite(PropertyDefinition property, @SuppressWarnings("rawtypes") IPrimitiveType value)
			throws Exception {
		this.setPropertyValue(property.getName(), value);
	}
	
}
