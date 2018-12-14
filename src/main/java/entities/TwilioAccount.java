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

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.NamedQuery;


/**
 * Entity class representing Twilio account credentials
 */
@Entity
@Table(name = "TwilioAccount")
@NamedQuery(name = "TwilioAccount.getAccountDetails", 
		query = "SELECT a FROM TwilioAccount a")
public class TwilioAccount 
{
	//PK for entity
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SID", nullable = false)
    private String sid;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "token", nullable = false)
    private String token;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;
    
    //default constructor
    public TwilioAccount()
    {
    	//not implemented
    }
    
    public String getSID()
    {
    	return sid;
    }
    
    public void setSID(String sid)
    {
    	this.sid = sid;
    }
    
    public String getToken()
    {
    	return token;
    }
    
    public void setToken(String token)
    {
    	this.token = token;
    }
    
    public String getPhoneNumber()
    {
    	return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber)
    {
    	this.phoneNumber = phoneNumber;
    }
    
    @Override
    public int hashCode()
    {
    	return Objects.hash(this.sid, this.token, this.phoneNumber);
    }
    
    @Override
    public String toString()
    {
    	return "Account Details {" + ", SID=" + sid
    			+ ", token=" + token
    			+ ", phoneNumber=" + phoneNumber;
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
        
        final TwilioAccount other = (TwilioAccount) obj;
        return Objects.equals(this.sid, other.sid)
                && Objects.equals(this.token, other.token)
                && Objects.equals(this.phoneNumber, other.phoneNumber);
    }    
}
