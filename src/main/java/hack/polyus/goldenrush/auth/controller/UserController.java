package hack.polyus.goldenrush.auth.controller;

import hack.polyus.goldenrush.auth.model.SecurityUser;
import hack.polyus.goldenrush.auth.model.User;
import hack.polyus.goldenrush.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {


    UserService userService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @ResponseBody
    @RequestMapping(
            value = "/user/save",
            consumes = "application/json",
            method = RequestMethod.POST
    )
    public void saveTeacher(
            @RequestBody User user,
            Errors errors
    ){
        SecurityUser ud = SecurityUser.getCurrent();
        if(!errors.hasErrors()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
        }
    }
}
