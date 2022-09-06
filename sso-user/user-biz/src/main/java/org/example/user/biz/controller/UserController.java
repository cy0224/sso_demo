package org.example.user.biz.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.example.common.entity.ResponseData;
import org.example.user.biz.mapper.UserMapper;
import org.example.user.biz.structmapper.UserToDTOMapper;
import org.example.user.data.entity.User;
import org.example.user.data.entity.UserDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    UserMapper userMapper;
    UserToDTOMapper userToDTOMapper;

    @PostMapping()
    public ResponseData<UserDTO> createUser(@RequestParam("name") String name, @RequestParam("password") String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Integer count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return ResponseData.fail("用户名已存在");
        }
        String salt = IdUtil.simpleUUID();
        String pwd = SecureUtil.md5(password + salt);
        User user = User.builder().name(name).pwd(pwd).salt(salt).build();
        userMapper.insert(user);
        return ResponseData.success(userToDTOMapper.from(userMapper.selectOne(queryWrapper)));
    }

    @GetMapping()
    public ResponseData<List<UserDTO>> getUsers(@RequestParam("size") Long size, @RequestParam("current") Long current) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        List<UserDTO> dtos = userPage.getRecords().stream().map(userToDTOMapper::from).collect(Collectors.toList());
        return ResponseData.success(dtos);
    }

    @GetMapping("/{id}")
    public ResponseData<UserDTO> getUserById(@PathVariable("id") Integer id) {
        User user = userMapper.selectById(id);
        return user == null ? ResponseData.fail("用户不存在") : ResponseData.success(userToDTOMapper.from(user));
    }

    @PutMapping("/{id}")
    public ResponseData<UserDTO> updateUser(@PathVariable("id") Integer id, @RequestParam("name") String name) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseData.fail("id:" + id + "用户不存在");
        } else {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", name);
            Integer count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                return ResponseData.fail("用户名已存在");
            }
            user.setName(name);
            userMapper.updateById(user);
            return ResponseData.success(userToDTOMapper.from(user));
        }
    }

    @DeleteMapping("{id}")
    public ResponseData<Object> deleteUser(@PathVariable("id") Integer id) {
        int i = userMapper.deleteById(id);
        if (i == 1) {
            return ResponseData.success(null);
        } else {
            return ResponseData.fail("删除失败");
        }
    }
}
