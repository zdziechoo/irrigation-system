package pl.ttpsc.irrigation.thing;

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
		@ThingworxPropertyDefinition(
				name="RouterLocation", 
				description="Router Location", 
				baseType="LOCATION",
				category = "Status", 
				aspects={
						"isReadOnly:FALSE",
						"pushType:VALUE",
						"isPersistent:TRUE"}),
})

public class IrrigationRouterThing extends VirtualThing{
	
	private final static String ROUTER_LOCATION = "RouterLocation";

	private Location routerLocation;
	
	public IrrigationRouterThing(String name, String description, ConnectedThingClient client) {
		super(name, description, client);
		super.initializeFromAnnotations();
		this.init();
	}
	
	private void init(){
		//Get the current values from the ThingWorx composer
		routerLocation = getRouterLocation();
		
	}
	
	@Override
	public void processScanRequest() throws Exception {
		super.processScanRequest();
		this.setLocationLodz();
		this.updateSubscribedProperties(15000);

	}
	
	private void setLocationLodz() throws Exception{
		this.routerLocation = new Location(19.4623015d,51.7462748d);
		LocationPrimitive loc = new LocationPrimitive(routerLocation);
		super.setProperty(ROUTER_LOCATION, loc);
	}
	
	public Location getRouterLocation() {
		return (Location) getProperty(ROUTER_LOCATION).getValue().getValue();
	}
	
	public void setRouterLocation() throws Exception{
		setProperty(ROUTER_LOCATION, new LocationPrimitive(this.routerLocation));
	}
	
	public void synchronizeState() {
		super.synchronizeState();
		super.syncProperties();
	}
	
	
	@Override
	public void processPropertyWrite(PropertyDefinition property, @SuppressWarnings("rawtypes") IPrimitiveType value)
			throws Exception {
		this.setPropertyValue(property.getName(), value);
	}
	
}
