/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.user.util.PasswordGenerator;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.exceptions.NotFoundException;

public class UserCreationBean
{
    private User user;
    private SessionBean sb;
    private boolean sendEmail = false;
    private static Logger logger = Logger.getLogger(UserCreationBean.class);

    public UserCreationBean()
    {
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.setUser(new User());
    }

    public String create() throws Exception
    {
        UserController uc = new UserController(sb.getUser());
        String regexEmailMatch = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        if (!user.getEmail().matches(regexEmailMatch))
        {
            BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        }
        else
        {
            try
            {
                if (uc.retrieve(user.getEmail()) != null)
                {
                    BeanHelper.error(sb.getMessage("error_user_already_exists"));
                }
            }
            catch (NotFoundException e)
            {
                PasswordGenerator generator = new PasswordGenerator();
                String password = generator.generatePassword();
                user.setEncryptedPassword(UserController.convertToMD5(password));
                uc.create(user);
                if (sendEmail)
                {
                    sendNewAccountEmail(password);
                }
                logger.info("New user created: " + user.getEmail());
                BeanHelper.info(sb.getMessage("success_user_create"));
            }
            catch (Exception e)
            {
                BeanHelper.error(sb.getMessage("error") + ": " + e);
            }
        }
        return "pretty:";
    }

    public void sendNewAccountEmail(String password)
    {
        EmailClient emailClient = new EmailClient();
        EmailMessages emailMessages = new EmailMessages();
        try
        {
            emailClient.sendMail(user.getEmail(), null, sb.getMessage("email_new_user_subject"),
                    emailMessages.getNewAccountMessage(password, user.getEmail(), user.getName()));
        }
        catch (Exception e)
        {
            logger.error("Error sending email", e);
            BeanHelper.error(sb.getMessage("error") + ": Email not sent");
        }
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public boolean isSysAdmin()
    {
        return sb.isAdmin();
    }

    public boolean isSendEmail()
    {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail)
    {
        this.sendEmail = sendEmail;
    }
}
