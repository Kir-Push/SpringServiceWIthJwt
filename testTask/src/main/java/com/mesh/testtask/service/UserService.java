package com.mesh.testtask.service;

import com.mesh.testtask.domain.entity.BaseResponse;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.domain.entity.UserDTO;
import com.mesh.testtask.domain.entity.UserRequest;
import com.mesh.testtask.domain.filter.UserFilter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {

    List<UserDTO> getUserList(UserFilter userFilter);

    List<User> getAllUsers();

    BaseResponse saveBulkUsers(List<User> userList);

    User getByName(String name);

    BaseResponse createUser(UserRequest request);

    BaseResponse editUser(UserRequest request);

    BaseResponse saveUser(User user);

    BaseResponse deleteUser(UserRequest request);
}
