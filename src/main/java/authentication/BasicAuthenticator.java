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
package authentication;

import database.UserDAO;
import entities.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for authenticating remote clients/users making use
 * of basic HTTP authentication.
 */
public class BasicAuthenticator implements Authenticator<BasicCredentials, User>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticator.class);
		
	/**
     * Reference to User DAO to check whether the user with credentials
     * specified exists in the application's backing database.
     */
    private final UserDAO userDAO;
	
    /**
     * Hibernate session factory; Necessary for the authenticate method to work,
     * which doesn't work as described in the documentation.
     */
    private final SessionFactory sessionFactory;
    
    public BasicAuthenticator(final UserDAO userDAO, final SessionFactory sessionFactory) 
    {
        this.userDAO = userDAO;
        this.sessionFactory = sessionFactory;
    }
    
    @UnitOfWork
    public final Optional<User> authenticate(BasicCredentials credentials)
            throws AuthenticationException 
    {
        Session session = sessionFactory.openSession();
        Optional<User> result;
        
        try 
        {
            ManagedSessionContext.bind(session);

            result = userDAO.findByEmail(credentials.getUsername());

            if (!result.isPresent()) 
            {
                return Optional.empty();
            } 
            else               
            {	
                if (credentials.getPassword().equals(result.get().getPassword()))
                {
                	LOGGER.info("User: {} logged in with email: {} and password: {}.",
                			result.get().getFirstName(), result.get().getEmail(),
                			result.get().getPassword());                	
                    return result;
                } 
                else 
                {
                    return Optional.empty();
                }
            }
        } 
        catch (Exception e) 
        {
            throw new AuthenticationException(e);
        } 
        finally 
        {
            ManagedSessionContext.unbind(sessionFactory);
            session.close();
        }

    }
}
