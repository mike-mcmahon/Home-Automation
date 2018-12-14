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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import database.AccountDAO;
import database.UserDAO;
import entities.TwilioAccount;
import entities.User;
import io.dropwizard.hibernate.UnitOfWork;
import representations.GpioSubject;


/**
 * This class defines the URLs and provides the interface to HTTP requests and works
 * with the class PiGpioService to set and retrieve information from the model.
 */
@Path("/dispatch")
@RolesAllowed("ADMIN")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class SMSResource implements GpioObserver
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SMSResource.class);
	private final AccountDAO accountDAO;
	private final UserDAO userDAO;
	private int count;
	private Timer timer;
	private boolean timerSet = false;
	private GpioSubject gpio;
	private final SessionFactory sessionFactory;
	
	public SMSResource(AccountDAO accountDAO, UserDAO userDAO, GpioSubject gpio, SessionFactory sessionFactory)
	{
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.gpio = gpio;
		this.sessionFactory = sessionFactory;
		this.gpio.registerObserver(this);
	}
	
	
	/**
	 * method called by GpioRepresentation class when there is a
	 * change in state on garage door.
	 * @param String state
	 */
	public void update(String state)
	{		
		if(state.equals("HIGH"))
		{
			
			//conditional to prevent multiple Timer objects
			if(!timerSet)
			{
				timer = new Timer();
				int delay = (20 * 60 * 1000);
				int period = (20 * 60 * 1000);
	    				
				//schedule first message to be sent when door is open 20 minutes.
				//then send a message every 20 minutes to a maximum of
				//3 times.
				MessageExecutor msgExec = new MessageExecutor();
				timer.schedule(msgExec, delay, period);
				timerSet = true;
			}
		}
		else
		{
			//cancel timer if the garage door is closed and reset the count
			timer.cancel();
			count = 0;
			timerSet = false;
		}
	}
	
	
	/**
	 * Send an SMS text message.
	 * FOR TESTING ONLY - Used for remote testing only.
	 */
	@POST
	@Path("/alertmessage")
	@UnitOfWork
	public Response sendSMSMessage()
	{
		List<User> users;
		List<TwilioAccount> accountDetails;
		int index = 0;
		
        users = userDAO.getAllUsers();
        accountDetails = accountDAO.getAccountDetails(); 
   
        Twilio.init(accountDetails.get(index).getSID(), accountDetails.get(index).getToken());
        
        for(User u : users)
        {
        	LOGGER.info("Test message sent to {} from URL /dispatch/alertmessage.", u.getFirstName());
        	@SuppressWarnings("unused")
			Message message = Message
                    .creator(new PhoneNumber(u.getPhoneNumber()), new PhoneNumber(accountDetails.get(index).getPhoneNumber()),
                    "This is a test message only.  Please ignore.")
                    .create();
        }
        return Response.ok("message(s) sent").build();
	}
	
	
	/**
	 * This private (inner) class is responsible for initiating a message
	 * being sent every 20 minutes to alert that the garage door has been
	 * left open.
	 */
	private class MessageExecutor extends TimerTask
	{
		@Override
		@UnitOfWork
		public void run()
		{
			if(count < 3)
			{
				//open a hibernate session.
				Session session = sessionFactory.openSession();
				try
				{
					//bind hibernate session, and get user/account details
					ManagedSessionContext.bind(session);
					List<User> users;
					List<TwilioAccount> accountDetails;
			        int index = 0;
					  
			        users = userDAO.getAllUsers();
			        accountDetails = accountDAO.getAccountDetails(); 
			        
			        Twilio.init(accountDetails.get(index).getSID(), accountDetails.get(index).getToken());
			        
			        for(User u : users)
			        {
			            LOGGER.info("Alert notification sent to {} for open garage door.", u.getName());
			            
			            @SuppressWarnings("unused")
						Message message = Message.creator(new PhoneNumber(u.getPhoneNumber()), 
			            		new PhoneNumber(accountDetails.get(index).getPhoneNumber()),
			                    u.getFirstName() + ", the garage door has been left open.  Please close it.")
			                    .create();    
			        }
			        //increment count
					count++;
				}
		        catch(Exception e)
				{
		        	
				}
		        finally
		        {
		        	//unbind and close session
		        	ManagedSessionContext.unbind(sessionFactory);
		            session.close();
		        }
			}
		}	
	}
}

