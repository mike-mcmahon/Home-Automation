/**
*	███╗   ███╗██╗██╗  ██╗███████╗    ███╗   ███╗ ██████╗███╗   ███╗ █████╗ ██╗  ██╗ ██████╗ ███╗   ██╗
*	████╗ ████║██║██║ ██╔╝██╔════╝    ████╗ ████║██╔════╝████╗ ████║██╔══██╗██║  ██║██╔═══██╗████╗  ██║
*	██╔████╔██║██║█████╔╝ █████╗      ██╔████╔██║██║     ██╔████╔██║███████║███████║██║   ██║██╔██╗ ██║
*	██║╚██╔╝██║██║██╔═██╗ ██╔══╝      ██║╚██╔╝██║██║     ██║╚██╔╝██║██╔══██║██╔══██║██║   ██║██║╚██╗██║
*	██║ ╚═╝ ██║██║██║  ██╗███████╗    ██║ ╚═╝ ██║╚██████╗██║ ╚═╝ ██║██║  ██║██║  ██║╚██████╔╝██║ ╚████║
*	╚═╝     ╚═╝╚═╝╚═╝  ╚═╝╚══════╝    ╚═╝     ╚═╝ ╚═════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝
*                                                                                                   
*	██████╗ ██████╗ ██████╗  ██████╗ ███████╗ █████╗ ██████╗     ██████╗ █████╗                        
*	╚════██╗██╔══██╗██╔══██╗██╔════╝ ██╔════╝██╔══██╗██╔══██╗   ██╔════╝██╔══██╗                       
*	 █████╔╝██████╔╝██║  ██║██║  ███╗█████╗  ███████║██████╔╝   ██║     ███████║                       
*	 ╚═══██╗██╔══██╗██║  ██║██║   ██║██╔══╝  ██╔══██║██╔══██╗   ██║     ██╔══██║                       
*	██████╔╝██║  ██║██████╔╝╚██████╔╝███████╗██║  ██║██║  ██║██╗╚██████╗██║  ██║                       
*	╚═════╝ ╚═╝  ╚═╝╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝ ╚═════╝╚═╝  ╚═╝         
*	
*	Copyright (C) 2017 Mike McMahon, A.Sc.T.
*
*	**********************************************************************
*	****    Raspberry Pi pi4j Home Automation and RESTful web service ****
*   ********************************************************************** 
*        
*   @author		Mike McMahon
*   @version	0.0.1
*   @since		2017-02-24
*   
*/
package resources;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import representations.*;
import org.glassfish.jersey.media.sse.*;

/**
 * This class defines the URLs and provides the interface to HTTP requests and works
 * with the class PiGpioService to set and retrieve information from the model.
 */
@Singleton
@Path("/homeservice")
@RolesAllowed({"ADMIN", "USER"})
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class HomeResource implements EventObserver
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeResource.class);
	private OnboardGpio onboardGpio;
	private SseBroadcaster broadcaster;
	private EventSubject eventSubject;
	
	public HomeResource(OnboardGpio gpio)
	{
		onboardGpio = gpio;
		eventSubject = gpio;
		broadcaster = new SseBroadcaster();
		this.eventSubject.registerEventObserver(this);
	}
	
	/**
	 * gets status information (one time/single http request) on the Raspberry Pi's
	 * I/O and returns it to the client.
	 * @return piService  
	 */
	@GET
	@Path("/iostatus_oneshot")
	public OnboardGpio getGpioStatus()
	{
		LOGGER.info("System status representation requested.");
		return onboardGpio.getUpdate();
	}
	
	/*
	 * Broadcasts messages to EventOutput and is trigger by IO state 
	 * changes in class GpioRepresentation.
	 * Implements interface EventObserver.
	 */
	public void broadcastIOUpdateMessage()
	{
		OnboardGpio iostatus = onboardGpio.getUpdate();
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		OutboundEvent event = eventBuilder.data(OnboardGpio.class, iostatus)
				.mediaType(MediaType.APPLICATION_XML_TYPE)
				.build();
												
		broadcaster.broadcast(event);
		
	}
	
	/**
	 * gets status information on the Raspberry Pi's
	 * I/O and returns it to the client on a continuous basis
	 * and only if it changes.
	 * @return EventOutput 
	 */
    @GET
    @Path("/iostatus")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getGpioStatusStream() 
    {
    	LOGGER.info("System status representation requested - SSE.");
       	final EventOutput eventOutput = new EventOutput();
        this.broadcaster.add(eventOutput);
        return eventOutput;
    }
	
	/**
	 * allows a client to remotely toggle the garage door relay
	 */
	@POST
	@Path("/garagerelay")
	public OnboardGpio garagDoorControl()
	{
		LOGGER.info("Garage Door - change state");
		onboardGpio.toggleGarageDoor();
		return onboardGpio.getUpdate();
	}
	
	/**
	 * allows the user to turn on and off relay two.
	 * this call returns a representation of the current state of the gpio.
	 * @param state
	 * @return piService
	 */
	@POST
	@Path("/zoneone")
	public OnboardGpio zoneOneControl()
	{
		LOGGER.info("Zone One - change state");
		onboardGpio.toggleZoneOne();
		return onboardGpio.getUpdate();
	}
	
	/**
	 * allows the user to turn on and off relay three.
	 * this call returns a representation of the current state of the gpio.
	 * @param state
	 * @return piService
	 */
	@POST
	@Path("/zonetwo")
	public OnboardGpio zoneTwoControl()
	{
		LOGGER.info("Zone Two - change state");
		onboardGpio.toggleZoneTwo();
		return onboardGpio.getUpdate();
	}
	
	/**
	 * allows the user to turn on and off relay three.
	 * this call returns a representation of the current state of the gpio.
	 * @param state
	 * @return piService
	 */
	@POST
	@Path("/zonethree")
	public OnboardGpio zoneThreeControl()
	{
		LOGGER.info("Zone Three - change state");
		onboardGpio.toggleZoneThree();
		return onboardGpio.getUpdate();
	}
	
}
