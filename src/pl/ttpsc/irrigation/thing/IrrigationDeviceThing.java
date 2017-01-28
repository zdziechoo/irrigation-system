package pl.ttpsc.irrigation.thing;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.LocationPrimitive;
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

	public IrrigationDeviceThing(String name, String description, ConnectedThingClient client) {
		super(name, description, client);
		super.initializeFromAnnotations();
	}
	
	private final static String WATER_PRESSURE = "WaterPressure";
	private final static String IRRIGATION_STRENGTH = "IrrigationStrength";
	private final static String LOCATION = "Location";
	private final static String IRRIGATION_STATE = "IrrigationState";
	private final static String ALARM_STATE = "AlarmState";
	
	@Override
	public void processScanRequest() throws Exception {
		
		super.processScanRequest();
		this.setWaterPressure();
		this.setIrrigationStrength();
		this.setLocation();
		this.setIrrigationState();
		this.setAlarmState();
		this.updateSubscribedProperties(15000);

	}
	
	private void setWaterPressure() throws Exception{
		double waterPressure = 50+Math.random()*50;
		super.setProperty(WATER_PRESSURE, waterPressure);	
	}
	
	private void setIrrigationStrength () throws Exception{
		double irrigationStrength = Math.random()*100;
		super.setProperty(IRRIGATION_STRENGTH, irrigationStrength);
	}
	
	private void setLocation() throws Exception{
		Location loc = new Location(40.8447819d, -73.8648268d, 14d);
		LocationPrimitive location = new LocationPrimitive(loc);
		super.setProperty(LOCATION, location);
	}
	
	private void setIrrigationState() throws Exception{
		boolean irrigationState = false;
		super.setProperty(IRRIGATION_STATE, irrigationState);
	}
	
	private void setAlarmState() throws Exception{
		Random generator = new Random();
		int alarmState = generator.nextInt(3);
		super.setProperty(ALARM_STATE, alarmState);
	}
	
	@Override
	public void processPropertyWrite(PropertyDefinition property, @SuppressWarnings("rawtypes") IPrimitiveType value)
			throws Exception {
		this.setPropertyValue(property.getName(), value);
	}
	
}
