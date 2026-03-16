package com.taskmagnet.service;

import com.taskmagnet.dto.UserRequest;
import com.taskmagnet.dto.UserResponse;

import java.util.List;

public interface IUserService {

    List<UserResponse> getAll();

    UserResponse getById(Long id);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
