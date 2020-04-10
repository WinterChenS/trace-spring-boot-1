package com.example.demo.core.service;

import com.example.demo.core.dao.UserDao;
import com.example.demo.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lastwhisper
 * @date 2019/12/11
 */
@Service(value = "userService")
public class UserService {

    @Autowired
    private UserDao userDao;

    public User findUserByUserService(User user) {
        return userDao.findUserByUserDao(user);
    }

}
