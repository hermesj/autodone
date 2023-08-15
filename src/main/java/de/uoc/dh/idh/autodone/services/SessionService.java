package de.uoc.dh.idh.autodone.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.uoc.dh.idh.autodone.entities.MastodonUser;
import de.uoc.dh.idh.autodone.repositories.MastodonUserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

/**
 * Handles a user session
 */

@Transactional
@Log4j2
@Service
public class SessionService{

    @Autowired
    HttpSession session;

    @Autowired
    MastodonService mastodonService;

    @Autowired
    MastodonUserRepository userRepository;


    /**
     * Adds the a some information about the user to the current Httpsession.
     * Is called at login.
     *
     * @param user
     */
    public void addActiveUser(MastodonUser user){

        Map<String ,String> activeuser = new HashMap<>();

        activeuser.put("id",user.getId()+"");
        activeuser.put("name",user.getName());
       // activeuser.put("admin",user.getAdmin());

        activeuser.put("profilePic",mastodonService.getProfilePicture(user.getOauthToken()));

        session.setAttribute("activeuser",activeuser);

    }


    /**
     * Removes the current active user from the Httpsession. Called at login or account deletion.
     */
    public void removeActiveUser(){
        session.removeAttribute("activeuser");
    }


    /**
     * Returns the current user in the Httpsession
     *
     * @return
     */
    public MastodonUser getActiveUser(){
        Map<String,String> userMap = (Map<String, String>) session.getAttribute("activeuser");
        if(userMap!=null){
            Integer userId = Integer.valueOf(userMap.get("id"));
            return userRepository.findMastodonUserById(userId);
        }  else return null;


    }


}
