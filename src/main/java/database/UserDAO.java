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
package database;

import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;

import entities.User;
import io.dropwizard.hibernate.AbstractDAO;


/**
 * User class data access object
 */
public class UserDAO extends AbstractDAO<User>
{
	/**
     * The constructor of user DAO which initializes Hibernate session factory
     * defined by the superclass.
     *
     * @param sessionFactory Hibernate session factory
     */
    public UserDAO(SessionFactory sessionFactory) 
    {
        super(sessionFactory);
    }
    
    
    /**
     * Method looks for a user with given credentials for authentication
     * purposes.
     *
     * @param email email used for login.
     * @param password password of a user.
     * @return An Optional containing the user if found or empty otherwise.
     */
    @SuppressWarnings("unchecked")
	public Optional<User> findByEmailAndPassword(String email, String password) 
    {
        return Optional.ofNullable(
                uniqueResult(namedQuery("User.findByEmailAndPassword")
                        .setParameter("email", email)
                        .setParameter("password", password)
                ));
    }
    
    /**
     * A method that finds a user by email. Used for testing purposes.
     *
     * @param email the email of a user.
     * @return The user characterized by the email passed to the method.
     */
    @SuppressWarnings("unchecked")
	public Optional<User> findByEmail(String email) 
    {
        return Optional.ofNullable(uniqueResult(namedQuery("User.findByEmail")
        		.setParameter("email", email)));
    }

    /**
     * Method looks for a user by password.
     *
     * @param password password used for login.
     * @return An Optional containing the user if found or empty otherwise.
     */
    @SuppressWarnings("unchecked")
	public Optional<User> findByPassword(String password) 
    {
        return Optional.ofNullable(uniqueResult(namedQuery("User.findByPassword")
                        .setParameter("password", password)));
    }
    
    /**
     * Method looks all users.
     *
     * @return An Optional containing all Users.
     */
    @SuppressWarnings("unchecked")
	public List<User> getAllUsers() 
    {
        return list(namedQuery("User.findAll"));
    }  
}
