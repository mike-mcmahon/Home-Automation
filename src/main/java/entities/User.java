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
package entities;

import java.security.Principal;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@Entity
@Table(name = "AuthenticatedUsers")
@NamedQueries(
		{
			@NamedQuery(name = "User.findByEmail",
					query = "SELECT u FROM User u WHERE u.email = :email"),
    
			@NamedQuery(name = "User.findByEmailAndPassword",
            	query = "SELECT u FROM User u WHERE u.email = :email AND u.password = :password"),
    
			@NamedQuery(name = "User.findByPassword",
            	query = "SELECT u FROM User u WHERE u.password = :password"),
			
			@NamedQuery(name = "User.findAll",
				query = "SELECT a FROM User a")
		}
)


/**
 * Defines an Authenticated application User.
 */
public class User implements Principal
{
	//PK for entity
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "firstName", nullable = false)
    private String firstName;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "lastName", nullable = false)
    private String lastName;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "role", nullable = false)
    private String role;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "activeStatus", nullable = false, length = 1)
    private String activeStatus;

    //default constructor
    public User() 
    {
    	//not used
    }
    
    //constructor
    public User(String firstName, String password, String role) 
    {
        this.firstName = firstName;
        this.password = password;
        this.role = role;
    }
    
    public String getEmail()
    {
    	return email;
    }
    
    public void setEmail(String email)
    {
    	this.email = email;
    }
    
    public String getFirstName()
    {
    	return firstName;
    }
    
    public void setFirstName(String firstName)
    {
    	this.firstName = firstName;
    }
    
    public String getLastName()
    {
    	return lastName;
    }
    
    public void setLastName(String lastName)
    {
    	this.lastName = lastName;
    }
    
    public String getPhoneNumber()
    {
    	return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber)
    {
    	this.phoneNumber = phoneNumber;
    }
    
    public String getRole()
    {
    	return role;
    }
    
    public void setRole(String role)
    {
    	this.role = role;
    }
    
    public String getPassword()
    {
    	return password;
    }
    
    public void setPassword(String password)
    {
    	this.password = password;
    }
    
    public String getActiveStatus()
    {
    	return activeStatus;
    }
    
    public void setActiveStatus(String activeStatus)
    {
    	this.activeStatus = activeStatus;
    }
    
    @Override
    public int hashCode()
    {
    	return Objects.hash(this.firstName, this.lastName, this.phoneNumber,
    			this.email, this.role, this.password, this.activeStatus);
    }
    
    @Override
    public String toString()
    {
    	return "User{" + ", firstName=" + firstName
    			+ ", lastName=" + lastName
    			+ ", phoneNumber=" + phoneNumber
    			+ ", email=" + email
    			+ ", role=" + role
    			+ ", password=" + password
    			+ ", activeStatus=" + activeStatus;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
        {
            return true;
        }
        
        if (obj == null) 
        {
            return false;
        }
        
        if (getClass() != obj.getClass()) 
        {
            return false;
        }
        
        final User other = (User) obj;
        return Objects.equals(this.firstName, other.firstName)
                && Objects.equals(this.lastName, other.lastName)
                && Objects.equals(this.phoneNumber, other.phoneNumber)
                && Objects.equals(this.email, other.email)
                && Objects.equals(this.role, other.role)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.activeStatus, other.activeStatus);
    }
    
    /**
     * Method implementation from Principal interface.
     *
     * @return The name of the Principal.
     */
    public String getName() {
        return firstName;
    }
}
