package cn.appsys.controller.loginController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.appsys.dao.backenduser.BackendUserMapper;
import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.service.developer.DevUserService;
import cn.appsys.tools.Constants;

@Controller
public class LoginController {
	private Logger logger = Logger.getLogger(LoginController.class); 
	@Resource
	private BackendUserService backendUserService;
	@Resource
	private DevUserService devUserService;

	/**
	 *跳转登录页面 后台管理员
	 */
	@RequestMapping(value="/admin/login.html")
	public String login(){
		return "backendlogin";
	}

	// 进行登录 开发人员
	@RequestMapping(value="/admin/dologin.html",method=RequestMethod.POST)
	public String doLogin(@RequestParam(value="userCode") String userCode,
			@RequestParam(value="userPassword") String userPassword,
			HttpSession session,
			HttpServletRequest request){
		logger.info("后台管理员登录=================");
		BackendUser backend = null; 
		try {
			backend = backendUserService.login(userCode, userPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != backend){
			session.setAttribute(Constants.USER_SESSION, backend);
			return "redirect:/admin/backend/main.html";
		}else{
			request.setAttribute("error", "用户名或密码错误");
			return "backendlogin";
		}	
	}
	//防止get请求进入405错误页面
	@RequestMapping(value="/admin/dologin.html",method=RequestMethod.GET)
	public String doLogin2(HttpServletRequest request){ 
		request.setAttribute("error", "请输入账号和密码进行登录");
		return "backendlogin";
	}	
	//登录成功跳转管理员页面
	@RequestMapping(value="/admin/backend/main.html")
	public String backendMain(){
		return "/backend/main";
	}
	//退出登录
	@RequestMapping(value="/admin/backend/loginout")
	public String backendLoginOut(HttpSession session,HttpServletRequest request){
		session.removeAttribute(Constants.USER_SESSION);
		/*if(null == session.getAttribute(Constants.USER_SESSION)){
			session.setAttribute("error", "退出成功");
		}*/
		return "redirect:/admin/login.html";
	}
	/**
	 * end 管理员登录
	 */

	/**
	 * 开发人员登录
	 */
	@RequestMapping(value="/dev/login.html")
	public String develogin(){
		return "devlogin";
	}

	//进行登录
	@RequestMapping(value="/dev/dologin.html",method=RequestMethod.POST)
	public String devDoLogin(@RequestParam(value="devCode") String devCode,
			@RequestParam(value="devPassword") String devPassword,
			HttpSession session,
			HttpServletRequest request){
		logger.info("开发人员登录=================");
		DevUser devUser = null;
		try {
			devUser = devUserService.login(devCode, devPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != devUser){
			session.setAttribute(Constants.DEV_USER_SESSION, devUser);
			return "redirect:/dev/flatform/main.html";
		}else{
			request.setAttribute("error", "用户名或密码错误");
			return "devlogin";
		}
	}
	//防止get请求 出现405错误页面
	@RequestMapping(value="/dev/dologin.html",method=RequestMethod.GET)
	public String davDoLogin2(HttpServletRequest request){ 
		request.setAttribute("error", "请输入账号和密码进行登录");
		return "devlogin";
	}

	//登录成功进去主页面
	@RequestMapping(value="/dev/flatform/main.html")
	public String davMain(){
		return "/developer/main";
	}
	
	//退出登录
	@RequestMapping(value="/dev/flatform/loginout")
	public String devLoginOut(HttpSession session,HttpServletRequest request){
		session.removeAttribute(Constants.DEV_USER_SESSION);
		/*if(null == session.getAttribute(Constants.USER_SESSION)){
			session.setAttribute("error", "退出成功");
		}*/
		return "redirect:/dev/login.html";
	}
}




