package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Category;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }


    @GetMapping ("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile getByUserId(Principal principal){
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        //returns the user by id
        return profileDao.getByUserId(user.getId());
    }

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile updateProfile(Principal principal, @RequestBody Profile profile){
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

        profileDao.updateProfile(user.getId(), profile);
        //returns the updated profile
        return profile;

    }
}
