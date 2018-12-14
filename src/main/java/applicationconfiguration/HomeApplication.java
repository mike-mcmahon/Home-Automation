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
package applicationconfiguration;

import database.AccountDAO;
import database.UserDAO;
import entities.TwilioAccount;
import entities.User;
import authentication.BasicAuthenticator;
import authentication.BasicAuthorizer;
import applicationhealth.ApplicationHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import representations.OnboardGpio;
import resources.HomeResource;
import resources.SMSResource;
import serialCommunications.SerialCommunications;
import serialCommunications.SerialCommunicationsManager;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;


/**
 * The core application class. This will help initialize the application and run.
 */
public class HomeApplication extends Application<AppConfiguration> 
{
    //Create Hibernate bundle.
    private final HibernateBundle<AppConfiguration> hibernateBundle
            = new HibernateBundle<AppConfiguration>(User.class, TwilioAccount.class) 
    			{
        			public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) 
        				{
        					return configuration.getDataSourceFactory();
        				}
    			};
	
	/** Application main entry point */
    public static void main(String[] args) throws Exception 
    {
        new HomeApplication().run(args);
    }

    /**
     * @param void
     * @return this applications name
     */
    @Override
    public String getName() 
    {
    	return "Home Automation";
    }

    /**
     * Initializes the application
     * @param bootstrap
     * @return void
     */
    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap)
    {
    	
        //Adding Hibernate bundle.
        bootstrap.addBundle(hibernateBundle);
    }
    
    /**
     * runs the application
     * @param appConfigurations
     * @param environment
     * @throws Exception
     */
    public void run(AppConfiguration appConfigurations, Environment environment) throws Exception 
    {	
    	// Create DAOs
    	final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
    	final AccountDAO accountDAO = new AccountDAO(hibernateBundle.getSessionFactory());
    	final OnboardGpio gpio = OnboardGpio.getInstance();
    	final HomeResource homeServices = new HomeResource(gpio);
    	final SMSResource smsMess = new SMSResource(accountDAO, userDAO, gpio, hibernateBundle.getSessionFactory());
    	final ApplicationHealthCheck healthCheck = new ApplicationHealthCheck(appConfigurations.getHealthCheck());
    	
    	// Create an authenticator which is using the backing database
        // to check credentials.
        final BasicAuthenticator authenticator = new UnitOfWorkAwareProxyFactory(hibernateBundle)
                .create(BasicAuthenticator.class, new Class<?>[]
                		{
                			UserDAO.class, SessionFactory.class
                		},
                        new Object[]
                        {
                        		userDAO,
                        		hibernateBundle.getSessionFactory()
                        }
                ); 
        
        // Register authenticator.
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(authenticator)
                .setAuthorizer(new BasicAuthorizer())
                .setRealm("AUTH-REALM")
                .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        
    	//Register the REST service
        environment.jersey().register(homeServices);
        environment.jersey().register(smsMess);
       
        //Register a health check
        environment.healthChecks().register("applicationHealth", healthCheck);
        
        //Add serial communications
        final SerialCommunications serial = new SerialCommunications(gpio);
        final SerialCommunicationsManager serialManager = new SerialCommunicationsManager(serial);
        environment.lifecycle().manage(serialManager);
  
    }
}