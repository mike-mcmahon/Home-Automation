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

import java.io.IOException;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;

/**
 * Class implements the serial port settings for ttyAMA0 on the Raspberry Pi3
 * and implements the transmit and receive functionality.
 */
public class SerialPort 
{
	private static SerialPort serialPortInstance = null;
	private Serial serialPort;
	private SerialCommands hmiCommands;
	
	/**
	 * Constructor implements an event listener for new, incoming serial messages.
	 * The listener then passes new messages to the SerialCommands object to be read and
	 * acted upon.
	 */
	private SerialPort(SerialCommands serialCommands)
	{
		serialPort = SerialFactory.createInstance();
		hmiCommands = serialCommands;
		
		try 
		{
            // create serial config object
            SerialConfig config = new SerialConfig();

            // set serial settings (device, baud rate, flow control, etc)    
            config.device("/dev/ttyAMA0")
                  .baud(Baud._115200)
                  .dataBits(DataBits._8)
                  .parity(Parity.NONE)
                  .stopBits(StopBits._1)
                  .flowControl(FlowControl.NONE);

            // open the default serial device/port with the configuration settings
            serialPort.open(config);

        }
        catch(IOException ex) 
		{
            ex.printStackTrace();
            return;
        }
		
		serialPort.addListener(new SerialDataEventListener() 
		{
	            @Override
	            public void dataReceived(SerialDataEvent serialEvent) 
				{

	                // print out the data received to the console
	                try 
					{
						hmiCommands.readMessage(serialEvent.getAsciiString());
	                } 
					catch (IOException e) 
					{
	                    e.printStackTrace();
	                }
	            }
	        });
	}
	
	/**
	 * Returns Singleton instance of this class.
	 * @param serialCommands
	 * @return SerialPort
	 */
	public static SerialPort getInstance(SerialCommands serialCommands)
	{
		if(serialPortInstance == null)
		{
			serialPortInstance = new SerialPort(serialCommands);
		}
		return serialPortInstance;
	}
	
	/**
	 * Transmit an ASCII string message to the connected HMI device.
	 */
	public void transmitMessage(String message)
	{
		
		try 
		{
			//write a formatted string to the serial transmit buffer.
			//message is already terminated with '\n' by message creator.
			serialPort.write(message);
		}
		catch(IllegalStateException | IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Close the serial port just before the object becomes out of scope.
	 */
	public void closePort()
	{
		try 
		{
			serialPort.close();
		} 
		catch (IllegalStateException | IOException e) 
		{
			e.printStackTrace();
		}
	}
}
