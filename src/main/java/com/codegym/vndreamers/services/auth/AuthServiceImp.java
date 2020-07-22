package com.codegym.vndreamers.services.auth;

import com.codegym.vndreamers.dtos.JWTResponse;
import com.codegym.vndreamers.exceptions.DatabaseException;
import com.codegym.vndreamers.exceptions.UserExistException;
import com.codegym.vndreamers.models.User;
import com.codegym.vndreamers.services.GenericCRUDService;
import com.codegym.vndreamers.services.auth.jwt.JWTIssuer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthServiceImp implements AuthService {
    private JWTIssuer jwtIssuer;

    private GenericCRUDService<User> userService;

    @Autowired
    public void setJwtIssuer(JWTIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    @Autowired
    public void setUserService(GenericCRUDService<User> userService) {
        this.userService = userService;
    }

    @Override
    public JWTResponse authenticate(User user) {
        return null;
    }

    @Override
    public JWTResponse register(User user) throws DatabaseException, UserExistException {
        JWTResponse jwtResponse = new JWTResponse();
        User userRegistered;
        try {
            userRegistered = saveUserToDB(user);
        } catch (SQLIntegrityConstraintViolationException exception) {
            throw new DatabaseException();
        }
        String accessToken = jwtIssuer.generateToken(userRegistered);
        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setUser(userRegistered);
        return jwtResponse;
    }

    private User saveUserToDB(User user) throws SQLIntegrityConstraintViolationException, UserExistException {
        String username;
        User userSaved;
        username = getUsernameFromEmail(user.getEmail());
        user.setUsername(username);
        userSaved = userService.save(user);
        return userSaved;
    }

    private String getUsernameFromEmail(String email) throws IndexOutOfBoundsException {
        Pattern pattern = Pattern.compile("@");
        Matcher matcher = pattern.matcher(email);
        int indexOfMatcher = matcher.find() ? matcher.start() : -1;
        return email.substring(0, indexOfMatcher);
    }
}