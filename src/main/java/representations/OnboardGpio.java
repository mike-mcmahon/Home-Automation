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
package representations;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.pi4j.gpio.extension.base.AdcGpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.spi.SpiChannel;
import resources.EventObserver;
import resources.GpioObserver;
import serialCommunications.SerialEventObserver;

/**
 * Class that models the physical Raspberry Pi resources and represents them in XML.
 * Access methods to read and write physical I/O state are synchronized to 
 * maintain consistent state of the GPIO when multiple users are accessing
 * the controls.
 * Only one object of this class is to ever exist, as it is a representation
 * of this physical real world device that it resides on.
 */
@XmlRootElement
public class OnboardGpio implements GpioSubject, EventSubject, SerialEventSubject 
{
	private static OnboardGpio onboardGpioRepresentation = null;
	private GpioObserver observingResource;
	private EventObserver eventObserver;
	private SerialEventObserver serialObserver;
	private final GpioController gpio;
	private final GpioPinDigitalInput garageDoorFeedback;
	private final GpioPinDigitalInput zoneOneFeedback;
	private final GpioPinDigitalInput zoneTwoFeedback;
	private final GpioPinDigitalInput zoneThreeFeedback;
	private final GpioPinDigitalOutput garageDoorRelay;
	private final GpioPinDigitalOutput zoneOneRelay;
	private final GpioPinDigitalOutput zoneTwoRelay;
	private final GpioPinDigitalOutput zoneThreeRelay;
    private final AdcGpioProvider analogInputProvider;
    private final GpioPinAnalogInput analogInput;
    
	
	@XmlElement
	private String garageDoorFeedbackState;
	
	@XmlElement
	private String zoneOneState;
	
	@XmlElement
	private String zoneTwoState;
	
	@XmlElement
	private String zoneThreeState;
	
	@XmlElement
	private String panelTemperature;
	
	@XmlTransient
	private String garageDoorRelayState;
	
	@XmlTransient
	private String zoneOneRelayState;
	
	@XmlTransient
	private String zoneTwoRelayState;
	
	@XmlTransient
	private String zoneThreeRelayState;
	
	/**
	 * Private constructor
	 * @throws IOException
	 */
	private OnboardGpio() throws IOException
	{
		//Get instance of gpio factory object.
		gpio = GpioFactory.getInstance();
		
		//Provision all digital inputs.
		garageDoorFeedback = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
		zoneOneFeedback = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
		zoneTwoFeedback = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		zoneThreeFeedback = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
		
		//Set the initial state of these objects in the XML representation.
		garageDoorFeedbackState = garageDoorFeedback.getState().toString();
		zoneOneState = zoneOneFeedback.getState().toString();
		zoneTwoState = zoneTwoFeedback.getState().toString();
		zoneThreeState = zoneThreeFeedback.getState().toString();
		
		//Provision the digital outputs for for all relays
		garageDoorRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
		zoneOneRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
		zoneTwoRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW);
		zoneThreeRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, PinState.LOW);
		
		//Set initial XML representation of relays state
		garageDoorRelayState = garageDoorRelay.getState().toString();
		zoneOneRelayState = zoneOneRelay.getState().toString();
		zoneTwoRelayState = zoneTwoRelay.getState().toString();
		zoneThreeRelayState = zoneThreeRelay.getState().toString();
		
		//Provision analog inputs for use with a MCP3008 ADC.
		analogInputProvider = new MCP3008GpioProvider(SpiChannel.CS0);
        analogInput = gpio.provisionAnalogInputPin(analogInputProvider, MCP3008Pin.CH0, "MyAnalogInput-CH0");

        //We need to monitor for a integer change of 1 (0-1023) as we are monitoring temperature.
        analogInputProvider.setEventThreshold(1, analogInput); 

        //Monitor the analog input every 1000 milliseconds.
        analogInputProvider.setMonitorInterval(1000);
        
		//Add a listener to the garageDoorInput.
		//After twenty minutes open, issue a text message to 
		//a list of recipients.
		garageDoorFeedback.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setGarageDoorFeedbackState();
				notifyEventObserver();
				notifyObserver();
				notifySerialEventObserver();
		    } 
		});
		
		//Add a listener to the zoneOneInput.
		zoneOneFeedback.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneOneState();
				notifyEventObserver();
				notifySerialEventObserver();
		    } 
		});
		
		//Add a listener to the zoneTwoInput.
		zoneTwoFeedback.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneTwoState();
				notifyEventObserver();
				notifySerialEventObserver();
		    } 
		});
		
		//Add a listener to the zoneThreeInput.
		zoneThreeFeedback.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneThreeState();
				notifyEventObserver();
				notifySerialEventObserver();
		    } 
		});
		
		//Add a listener to the garageDoorRelay.
		garageDoorRelay.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setGarageDoorRelayState();
				notifyEventObserver();
		    } 
		});
		
		//Add a listener to the zoneOneRelay.
		zoneOneRelay.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneOneRelayState();
				notifyEventObserver();
		    } 
		});
		
		//Add a listener to the zoneTwoRelay.
		zoneTwoRelay.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneTwoRelayState();
				notifyEventObserver();
		    } 
		});
		
		//Add a listener to the zoneThreeRelay.
		zoneThreeRelay.addListener(new GpioPinListenerDigital() 
		{
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
			{
				setZoneThreeRelayState();
				notifyEventObserver();
		    } 
		});
		
        //Create an analog pin value change listener.
		//Add the listener to the analogInput.
		//Update panelTemperature on a change event.
        analogInput.addListener(new GpioPinListenerAnalog()
        {
        		public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event)
        		{
            		setPanelTemperature(event.getValue());
            		notifyEventObserver();  
            		notifySerialEventObserver();
        		}
        });   
        
        //Set the initial value of panelTemperature
        //method located at end of the constructor because
        //if located earlier, the field does not get set.
        setPanelTemperature(analogInput.getValue());
	}

	/**
	 * Returns instance of this class
	 * @return GpioRepresentation
	 * @throws IOException 
	 */
	public static OnboardGpio getInstance() throws IOException
	{
		if(onboardGpioRepresentation == null)
		{
			onboardGpioRepresentation = new OnboardGpio();
		}
		return onboardGpioRepresentation;
	}
	
	/**
	 * Registers SMSResource as an observer object
	 * @param GpioObserver
	 */
	public void registerObserver(GpioObserver resource)
	{
		observingResource = resource;
	}
	
	/**
	 * Registers HomeResource as an observer object
	 * @param EventObserver
	 */
	public void registerEventObserver(EventObserver observer)
	{
		eventObserver = observer;
	}
	
	/**
	 * Registers SerialCommunications as an observer object
	 * @param SerialEventObserver
	 */
	public void registerSerialEventObserver(SerialEventObserver serial)
	{
		this.serialObserver = serial;
	}
	
	/**
	 * Notifies SMSResource observer object of change to garage door state
	 */
	public void notifyObserver()
	{
		observingResource.update(getGarageDoorInputState());
	}
	
	/**
	 * Notifies HomeResource observer object of IO state changes
	 */
	public void notifyEventObserver()
	{
		eventObserver.broadcastIOUpdateMessage();
	}
	
	/**
	 * 
	 */
	public void notifySerialEventObserver()
	{
		serialObserver.transmitSerialEvent();
	}
	
	/**
	 * Returns an object of this class with the current
	 * representation of the I/O states
	 * @return this
	 */
	public synchronized OnboardGpio getUpdate() 
	{
		this.getGarageDoorInputState();
		this.getZoneOneFeedback();
		this.getZoneTwoFeedback();
		this.getZoneThreeFeedback();
		this.getGarageDoorRelayState();
		this.getZoneOneRelayState();
		this.getZoneTwoRelayState();
		this.getZoneThreeRelayState();
		this.getPanelTemperature();
		return this;
	}
	
	/**
	 * returns the state of the garage door input
	 * @return garageDoorState
	 */
	public synchronized String getGarageDoorInputState()
	{
		return garageDoorFeedbackState;
	}
	
	/**
	 * returns the state of the input
	 * @return zoneOneState
	 */
	public synchronized String getZoneOneFeedback()
	{
		return zoneOneState;
	}
	
	/**
	 * returns the state of the input
	 * @return zoneTwoState
	 */
	public synchronized String getZoneTwoFeedback()
	{
		return zoneTwoState;
	}
	
	/**
	 * returns the state of the input
	 * @return zoneThreeState
	 */
	public synchronized String getZoneThreeFeedback()
	{
		return zoneThreeState;
	}
	
	/**
	 * returns the XML representation for the state
	 * of the output relay.
	 * @return garageDoorRelayState
	 */
	@XmlElement
	private synchronized String getGarageDoorRelayState()
	{
		return garageDoorRelayState;
	}
	
	/**
	 * returns the XML representation for the state
	 * of the output relay.
	 * @return zoneOneRelayState
	 */
	@XmlElement
	private synchronized String getZoneOneRelayState()
	{
		return zoneOneRelayState;
	}
	
	/**
	 * returns the XML representation for the state
	 * of the output relay.
	 * @return zoneTwoRelayState
	 */
	@XmlElement
	private synchronized String getZoneTwoRelayState()
	{
		return zoneTwoRelayState;
	}
	
	/**
	 * returns the XML representation for the state
	 * of the output relay.
	 * @return zoneThreeRelayState
	 */
	@XmlElement
	private synchronized String getZoneThreeRelayState()
	{
		return zoneThreeRelayState;
	}
	
	/**
	 * returns the current panel temperature
	 * as measured my a TMP36 via ADC MCP3008.
	 * @return panelTemperature
	 */
	public synchronized String getPanelTemperature()
	{
		return panelTemperature;
	}
	
	/**
	 * pulses the garage door output relay
	 */
	public synchronized void toggleGarageDoor()
	{
		garageDoorRelay.pulse(1000);
	}
	
	/**
	 * pulses the zone one output relay for 0.5 second
	 */
	public synchronized void toggleZoneOne()
	{
			zoneOneRelay.pulse(500);
	}
	
	/**
	 * pulses the zone two output relay for 0.5 second
	 */
	public synchronized void toggleZoneTwo()
	{
			zoneTwoRelay.pulse(500);
	}
	
	/**
	 * pulses the zone three output relay for 0.5 second
	 */
	public synchronized void toggleZoneThree()
	{
			zoneThreeRelay.pulse(500);
	}
	
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setGarageDoorFeedbackState()
	{
		garageDoorFeedbackState = garageDoorFeedback.getState().toString();
	}
	
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneOneState()
	{
		zoneOneState = zoneOneFeedback.getState().toString();
	}
	
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneTwoState()
	{
		zoneTwoState = zoneTwoFeedback.getState().toString();
	}
	
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneThreeState()
	{
		zoneThreeState = zoneThreeFeedback.getState().toString();
	}
	 
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setGarageDoorRelayState()
	{
		garageDoorRelayState = garageDoorRelay.getState().toString();
	}
	 
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneOneRelayState()
	{
		zoneOneRelayState = zoneOneRelay.getState().toString();
	}
	 
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneTwoRelayState()
	{
		zoneTwoRelayState = zoneTwoRelay.getState().toString();
	}
	 
	/*
	 * Automatically set the state value via EventListener.
	 */
	private void setZoneThreeRelayState()
	{
		zoneThreeRelayState = zoneThreeRelay.getState().toString();
	}
	 
	/**
	 * Calculate and set panel temperature.
	 * @param aDCRawValue
	 */
	private synchronized void setPanelTemperature(double aDCRawValue)
	{
		double millivolts = aDCRawValue * (3300.0 / 1023.0);
		double tempCelsius = (millivolts - 500.0) / 10.0;
		BigDecimal temperature = new BigDecimal(String.valueOf(tempCelsius));
		panelTemperature = String.valueOf(temperature.setScale(2, RoundingMode.HALF_EVEN));
		
	}
	 
}
