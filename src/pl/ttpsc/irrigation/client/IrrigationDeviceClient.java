package pl.ttpsc.irrigation.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.common.SecurityClaims;

import pl.ttpsc.irrigation.thing.IrrigationDeviceThing;


public class IrrigationDeviceClient extends ConnectedThingClient{
	
	private static final Logger LOG = LoggerFactory.getLogger(IrrigationDeviceClient.class);	
	
	private static final String URI = "ws://localhost:8080/Thingworx/WS";
	private static final String APP_KEY = "9bd490b6-a898-4885-a89a-744fea1a5e26";
	private static final int TIMEOUT = 30000;
	private static final int TIME_BREAK = 15000;
	
	private static final String THINGNAME_1 = "ID-Lodz-1";
	private static final String THINGNAME_2 = "ID-Lodz-2";
	private static final String THINGNAME_3 = "ID-Lodz-3";
	
	
	public IrrigationDeviceClient(ClientConfigurator config) throws Exception {
		super(config);
	}
	
	public static void main(String[] args) throws Exception {
		ClientConfigurator config = new ClientConfigurator();
		config.setUri(URI);
		config.ignoreSSLErrors(true); 
		
		SecurityClaims claims = SecurityClaims.fromAppKey(APP_KEY);
		config.setSecurityClaims(claims);
		
		try{
			IrrigationDeviceClient client = new IrrigationDeviceClient(config);
			client.start();
			if(client.waitForConnection(TIMEOUT)){
				LOG.info("The client is now connected");
						
				IrrigationDeviceThing irrigationDeviceThing1 = new IrrigationDeviceThing(THINGNAME_1, "Deliver signals from IrrigationDevice thing in ThingWorx composer", client);
				IrrigationDeviceThing irrigationDeviceThing2 = new IrrigationDeviceThing(THINGNAME_2, "Deliver signals from IrrigationDevice thing in ThingWorx composer", client);
				IrrigationDeviceThing irrigationDeviceThing3 = new IrrigationDeviceThing(THINGNAME_3, "Deliver signals from IrrigationDevice thing in ThingWorx composer", client);
				client.bindThing(irrigationDeviceThing1);
				client.bindThing(irrigationDeviceThing2);
				client.bindThing(irrigationDeviceThing3);
				
					while (!client.isShutdown()) {
					Thread.sleep(TIME_BREAK);
					for (VirtualThing vt : client.getThings().values()) {
						vt.processScanRequest();
					}
				}
			}
			else {
				LOG.warn("Client did not connected within timeout. Exiting");
			}
			client.shutdown();
		}
		catch (Exception e) {
			LOG.error("An exception while initializing the client",e);
		}
		LOG.info("Irrigation system agent is done. Exiting");
	}
}
	
