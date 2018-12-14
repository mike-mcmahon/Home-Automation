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
package serialCommunications;

import representations.OnboardGpio;

/**
 * Provide capability to read new messages received by the serial port and execute the corresponding commands,
 * and to build and transmit messages via the serial port to the control panel HMI.
 */
public class SerialCommands
{
	private OnboardGpio onboardGpio;
	private StringBuilder[] receivedCommands;
	private StringBuilder[] transmitCommands;
	private StringBuilder transmitMessage;
	private StringBuilder transmitHeartBeat;
	private final StringBuilder delimiter;
	private final StringBuilder heartBeat;
	private final StringBuilder nonValue;
	private StringBuilder garage;
	private StringBuilder zoneOne;
	private StringBuilder zoneTwo;
	private StringBuilder zoneThree;
	
	public SerialCommands(OnboardGpio gpio)
	{
		//create and initialize and empty receivedCommands buffer.
		receivedCommands = new StringBuilder[4];
		for (int i = 0; i < receivedCommands.length; i++)
		{
			receivedCommands[i] = new StringBuilder("");
		}
		
		//create and intialize a transmitCommands buffer.
		transmitCommands = new StringBuilder[5];
		for (int j = 0; j < transmitCommands.length; j++)
		{
			transmitCommands[j] = new StringBuilder("");
		}
		
		this.onboardGpio = gpio;
		transmitMessage = new StringBuilder("");
		transmitHeartBeat = new StringBuilder("");
		delimiter = new StringBuilder(",");
		heartBeat = new StringBuilder("1");
		nonValue = new StringBuilder("333");
		garage = new StringBuilder("");
		zoneOne = new StringBuilder("");
		zoneTwo = new StringBuilder("");
		zoneThree = new StringBuilder("");
		
	}
	
	/**
	 * build a comma delimited serial command string and return it to the caller. 
	 * Data to be transmitted is either the string representation of a "1" or "0" for boolean values, 
	 * or the string representation of a floating point value.
	 */
	public String createMessage()
	{		
			//get the components of the message
			garage.replace(0, garage.length(), onboardGpio.getGarageDoorInputState());
			if(garage.toString().equals("LOW"))
			{
				transmitCommands[0].replace(0, transmitCommands[0].length(), "0");
			}
			if(garage.toString().equals("HIGH"))
			{
				transmitCommands[0].replace(0, transmitCommands[0].length(), "1");
			}
			
			zoneOne.replace(0, zoneOne.length(), onboardGpio.getZoneOneFeedback());
			if(zoneOne.toString().equals("LOW"))
			{
				transmitCommands[1].replace(0, transmitCommands[1].length(), "0");
			}
			if(zoneOne.toString().equals("HIGH"))
			{
				transmitCommands[1].replace(0, transmitCommands[1].length(), "1");
			}
			
			zoneTwo.replace(0, zoneTwo.length(), onboardGpio.getZoneTwoFeedback());
			if(zoneTwo.toString().equals("LOW"))
			{
				transmitCommands[2].replace(0, transmitCommands[2].length(), "0");
			}
			if(zoneTwo.toString().equals("HIGH"))
			{
				transmitCommands[2].replace(0, transmitCommands[2].length(), "1");
			}
			
			zoneThree.replace(0, zoneThree.length(), onboardGpio.getZoneThreeFeedback());
			if(zoneThree.toString().equals("LOW"))
			{
				transmitCommands[3].replace(0, transmitCommands[3].length(), "0");
			}
			if(zoneThree.toString().equals("HIGH"))
			{
				transmitCommands[3].replace(0, transmitCommands[3].length(), "1");
			}
			
			transmitCommands[4].replace(0, transmitCommands[4].length(), onboardGpio.getPanelTemperature());
			
			//clear any previous message
			transmitMessage.replace(0, transmitMessage.length(), "");
			
			//build the new message
			int i;
			for (i = 0; i < transmitCommands.length; i++)
			{
				transmitMessage.append(transmitCommands[i]);
				transmitMessage.append(delimiter);
			}
			transmitMessage.append(heartBeat);
			transmitMessage.append(delimiter);
			transmitMessage.append("\n");
			
			//return new message to caller
			return transmitMessage.toString();
		
	}
	
	/**
	 * Create a message with a heartbeat value.
	 */
	public String createHeartBeatMessage()
	{
		//clear any previous message
		transmitHeartBeat.replace(0, transmitHeartBeat.length(), "");
			
		//build the new message
		int i;
		for (i = 0; i < 5; i++)
		{
			transmitHeartBeat.append(nonValue);
			transmitHeartBeat.append(delimiter);
		}
			transmitHeartBeat.append(heartBeat);
			transmitHeartBeat.append(delimiter);
			transmitHeartBeat.append("\n");
			
			return transmitHeartBeat.toString();
	}
	
	/**
	 * Received command strings from the HMI are always four values of either a "1", or a "0".  
	 * The received commands are always in the order of garagedoor, zone 1, zone 2, and zone 3.  
	 * These are boolean commands for on or off.
	 */
	public void readMessage(String receivedMessage)
	{
		//parse the received ASCII string, and place individual commands into an array.
		String[] messagesReceived = receivedMessage.split(",");
		
		//clear the receivedCommand array.
		int i;
		for (i = 0; i < receivedCommands.length; i++)
		{
			receivedCommands[i].replace(0, receivedCommands[i].length(), messagesReceived[i]);
		}	
		
		executeCommands();
	}
	
	/**
	 * After a new message is received by the serial port and read, execute the commands received.
	 */
	public void executeCommands()
	{
		//open/close the garage door
		String garage = receivedCommands[0].toString();
		if(garage.equals("1"))
		{
			onboardGpio.toggleGarageDoor();
		}
		
		//turn on/off zone one lighting
		String zoneOne = receivedCommands[1].toString();
		if(zoneOne.equals("1"))
		{
			onboardGpio.toggleZoneOne();
		}
		
		//turn on/off zone two lighting
		String zoneTwo = receivedCommands[2].toString();
		if(zoneTwo.equals("1"))
		{
			onboardGpio.toggleZoneTwo();
		}
		
		//turn on/off zone three lighting
		String zoneThree = receivedCommands[3].toString();
		if(zoneThree.equals("1"))
		{
			onboardGpio.toggleZoneThree();
		}
	}
	
}
