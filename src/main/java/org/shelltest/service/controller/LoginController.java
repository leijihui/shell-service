package org.shelltest.service.controller;

import org.shelltest.service.entity.Property;
import org.shelltest.service.exception.LoginException;
import org.shelltest.service.services.PropertyService;
import org.shelltest.service.utils.BASE64Util;
import org.shelltest.service.utils.Constant;
import org.shelltest.service.services.LoginAuth;
import org.shelltest.service.utils.ResponseBuilder;
import org.shelltest.service.utils.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    PropertyService propertyService;
    @Autowired
    LoginAuth loginAuth;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/login")
    public ResponseEntity login (String username, String password) throws LoginException {
        String token = null;
        if (username == null || password == null) {
            throw new LoginException("登录失败，请重新登录");
        }
        else {
            Property loginInfo = propertyService.getPropertyByKeys("LOGIN", username);
            if (loginInfo == null)
                throw new LoginException(Constant.ResultCode.NOT_FOUND, "用户名错误");
            String enc = BASE64Util.encode(password.trim());
            if (!enc.equals(loginInfo.getVal()))
                throw new LoginException(Constant.ResultCode.NOT_FOUND, "密码错误");
            //logger.debug("登录成功，用户："+username+" "+Base64Utils.decode(loginInfo.getVal().getBytes()));
            token = loginAuth.createToken(username);
        }
        return new ResponseBuilder().putItem("token", token).putItem("expiration", loginAuth.getExpiration().getTime()).getResponseEntity();
    }
}