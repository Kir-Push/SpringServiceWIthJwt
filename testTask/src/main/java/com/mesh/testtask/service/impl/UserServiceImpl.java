package com.mesh.testtask.service.impl;

import com.mesh.testtask.dao.UserRepository;
import com.mesh.testtask.domain.entity.BaseResponse;
import com.mesh.testtask.domain.entity.Phone;
import com.mesh.testtask.domain.entity.Profile;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.domain.entity.UserDTO;
import com.mesh.testtask.domain.entity.UserRequest;
import com.mesh.testtask.domain.filter.FilterCriteria;
import com.mesh.testtask.domain.filter.UserFilter;
import com.mesh.testtask.domain.filter.UserSpecification;
import com.mesh.testtask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUserList(UserFilter userFilter) {
        List<User> userList;
        if (userFilter == null) {
            userList = userRepository.findAll();
            return userList.stream().map(this::convertToDTO).collect(Collectors.toList());
        }
        List<UserSpecification> filters = new ArrayList<>();
        if (userFilter.getName() != null) {
            filters.add(new UserSpecification(new FilterCriteria("name", "like", userFilter.getName())));
        }
        if (userFilter.getEmail() != null) {
            filters.add(new UserSpecification(new FilterCriteria("email", "like", userFilter.getEmail())));
        }
        if (userFilter.getAge() != null) {
            filters.add(new UserSpecification(new FilterCriteria("age", ">", userFilter.getAge())));
            filters.add(new UserSpecification(new FilterCriteria("age", "<", userFilter.getAge())));
        }
        if (userFilter.getPhone() != null) {
            filters.add(new UserSpecification(new FilterCriteria("phone", "like", userFilter.getPhone())));
        }
        Specification<User> where = null;
        if (filters.size() > 0) {
            where = Specification.where(filters.get(0));
            for (int i = 1; i < filters.size(); i++) {
                where = where.and(filters.get(i));
            }
        }
        PageRequest of = null;
        if (userFilter.getPage() != null && userFilter.getPageSize() != null) {
            of = PageRequest.of(userFilter.getPage(), userFilter.getPageSize());
        }
        if (where != null && of != null) {
            userList = userRepository.findAll(where, of).getContent();
        } else if (where == null && of != null) {
            userList =  userRepository.findAll(of).getContent();
        } else if (where != null) {
            userList =  userRepository.findAll(where);
        } else {
            userList = userRepository.findAll();
        }

        return userList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public BaseResponse saveBulkUsers(List<User> userList) {
        try {
            userRepository.saveAllAndFlush(userList);
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            return new BaseResponse(e.getMessage(), false);
        }
        return new BaseResponse();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        List<String> phone = new ArrayList<>();
        for (Phone userPhone : user.getPhones()) {
            phone.add(userPhone.getValue());
        }
        userDTO.setPhone(phone);
        userDTO.setEmail(user.getEmail());
        userDTO.setAge(user.getAge());
        Profile profile = user.getProfile();
        if (profile != null) {
            userDTO.setAmount(profile.getCash() == null ? profile.getAmount() : profile.getCash());
        }
        return userDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public User getByName(String name) {
        Optional<User> result = userRepository.findByName(name);
        return result.orElse(null);
    }

    @Override
    @Transactional()
    public BaseResponse createUser(UserRequest request) {
        if (!validate(request)){
            return new BaseResponse("Invalid request", false);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPassword(request.getPassword());

        Set<Phone> phones = new HashSet<>();
        for (String phoneNum : request.getPhone()) {
            Phone phone = new Phone();
            phone.setUser(user);
            phone.setValue(phoneNum);
            phones.add(phone);
        }
        user.setPhones(phones);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setAmount(request.getAmount());
        user.setProfile(profile);
        return saveUser(user);
    }

    @Transactional()
    @Override
    public BaseResponse editUser(UserRequest request) {
        if (request == null || request.getId() == null || request.getEmail() == null) {
            return new BaseResponse("Invalid request", false);
        }

        // Just for test case.
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail.isPresent()) {
            return new BaseResponse("Mail already present", false);
        }
        Optional<User> byId = userRepository.findById(request.getId());
        if (byId.isEmpty()) {
            return new BaseResponse("User Not existed", false);
        }
        User user = byId.get();
        user.setEmail(request.getEmail()); // change email only in test task.
        return saveUser(user);
    }

    @Override
    @Transactional
    public BaseResponse saveUser(User user) {
        if (user == null) {
            LOG.error("Invalid request");
            return new BaseResponse("Invalid request", false);
        }
        try {
            userRepository.saveAndFlush(user);
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            return new BaseResponse(e.getMessage(), false);
        }
        return new BaseResponse();
    }

    @Override
    @Transactional()
    public BaseResponse deleteUser(UserRequest request) {
        if (request == null || request.getId() == null) {
            LOG.error("Invalid request");
            return new BaseResponse("Invalid request", false);
        }
        try {
            userRepository.deleteById(request.getId());
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new BaseResponse(e.getMessage(), false);
        }
        return new BaseResponse();
    }

    private boolean validate(UserRequest request){
        return request != null && request.getName() != null
                && request.getEmail() != null && request.getAge() != null &&
                request.getAmount() != null && request.getPassword() != null &&
                request.getPhone() != null;
    }


}
