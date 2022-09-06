package org.example.auth.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.mapper.UserMapper;
import org.example.common.entity.ResponseData;
import org.example.user.data.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private UserMapper userMapper;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public ResponseData<String> login(@RequestParam("name") String name, @RequestParam("password") String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return ResponseData.fail("用户名或密码错误");
        } else {
            String pwd = SecureUtil.md5(password + user.getSalt());
            if (!Objects.equals(pwd, user.getPwd())) {
                return ResponseData.fail("用户名或密码错误");
            } else {
                String strId = String.valueOf(user.getId());
                Object oldToken = redisTemplate.opsForValue().get(strId);
                if (oldToken instanceof String) {
                    redisTemplate.delete(Arrays.asList((String) oldToken, strId));
                }
                String token = IdUtil.simpleUUID();
                redisTemplate.opsForValue().set(token, user);
                redisTemplate.expire(token, 1, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(strId, token);
                redisTemplate.expire(strId, 1, TimeUnit.MINUTES);
                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(60);
                cookie.setPath("/");
                response.addCookie(cookie);
                return ResponseData.success(token);
            }
        }
    }

    @GetMapping("/token")
    public ResponseData<String> checkToken(@RequestParam("token") String token) {
        Object obj = redisTemplate.opsForValue().get(token);
        if (obj instanceof User) {
            User user = (User) obj;
            log.info("login user---->" + user);
            redisTemplate.expire(token, 1, TimeUnit.MINUTES);
            redisTemplate.expire(String.valueOf(user.getId()), 1, TimeUnit.MINUTES);
            return ResponseData.success("success");
        } else {
            return ResponseData.fail("token不存在");
        }
    }

    @GetMapping("/logout")
    public ResponseData<String> logout() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            Arrays.stream(cookies).filter(ck -> "token".equals(ck.getName())).findFirst().ifPresent(ck -> {
                String oldToken = ck.getValue();
                Object obj = redisTemplate.opsForValue().get(oldToken);
                if (obj instanceof User) {
                    String strId = String.valueOf(((User) obj).getId());
                    redisTemplate.delete(Arrays.asList(ck.getValue(), strId));
                }
            });
        }
        return ResponseData.success("success");
    }
}
