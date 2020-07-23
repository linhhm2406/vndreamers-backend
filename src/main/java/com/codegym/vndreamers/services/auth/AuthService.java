package com.codegym.vndreamers.services.auth;

import com.codegym.vndreamers.dtos.JWTResponse;
import com.codegym.vndreamers.dtos.LoginRequest;
import com.codegym.vndreamers.exceptions.DatabaseException;
import com.codegym.vndreamers.exceptions.UserExistException;
import com.codegym.vndreamers.models.User;

public interface AuthService {
    JWTResponse authenticate(LoginRequest loginRequest) throws UserExistException;

    User register(User user) throws DatabaseException, UserExistException;
}
