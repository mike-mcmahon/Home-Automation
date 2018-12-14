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

import java.util.Timer;
import java.util.TimerTask;
import representations.OnboardGpio;
import representations.SerialEventSubject;

/**
 * Class that acts as a serial communications client and is managed by the
 * http container environment.
 * Class is used to negotiate serial communications with the control panel
 * HMI device.
 */
public class SerialCommunications implements SerialEventObserver
{
	private final SerialPort serialPort;
	private SerialCommands serialCommands;
	private SerialEventSubject serialEvent;
	private Timer timer;
	private HeartBeat heartBeat;
	private int delay;
	private int period;
	
	public SerialCommunications(OnboardGpio gpio)
	{
		serialEvent = gpio;
		serialCommands = new SerialCommands(gpio);
		serialPort = SerialPort.getInstance(serialCommands);
		this.serialEvent.registerSerialEventObserver(this);
		
		timer = new Timer();
		//5 minute delay and period
		delay = (5 * 60 * 1000);
		period = (5 * 60 * 1000);
		
		heartBeat = new HeartBeat();
		timer.schedule(heartBeat, delay, period);
	}
	
	/**
	 * Send a serial message to HMI whenever gpio changes state
	 */
	public void transmitSerialEvent() 
	{
		serialPort.transmitMessage(serialCommands.createMessage());
	}
	
	/**
	 * Close the serial port.
	 */
	public void close()
	{
		serialPort.closePort();
	}
	
	/**
	 * Cancel the active timer.
	 */
	public void cancelTimer()
	{
		timer.cancel();
	}
	
	/**
	 * Private inner class that provides the task the timer is supposed to
	 * execute every period.
	 */
	private class HeartBeat extends TimerTask
	{
		public void run()
		{
			serialPort.transmitMessage(serialCommands.createHeartBeatMessage());
		}
	}
}
