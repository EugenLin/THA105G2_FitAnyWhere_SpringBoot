package com.fitanywhere.user.controller;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fitanywhere.user.model.UserService;
import com.fitanywhere.user.model.UserVO;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
    private UserService userService;
	
	@GetMapping("/user_login")
    public String userLogin() {
        return "front-end/user/user_login";
    }
	
	@GetMapping("/force_user_login")
	public ModelAndView userForceLogin() {
	    ModelAndView modelAndView = new ModelAndView("front-end/user/user_login");
	    modelAndView.addObject("loginMessage", "您需要登入才能訪問此頁面。");
	    System.out.println("loginMessage: " + modelAndView.getModel().get("loginMessage"));
	    return modelAndView;
	}
	
	@GetMapping("/test")
    public String testPage() {
        return "front-end/user/test";
    }
	
	
//	Andy
	@GetMapping("/coach_profile")
    public UserVO profile(ModelMap model) {
		
		Integer uId = 10001;
	    UserVO userVO = userService.getUser(uId);
	    return userVO;
    }



}
