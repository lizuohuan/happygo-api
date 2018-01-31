package com.magicbeans.happygo.util;

import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class LoginHelper {
	
	public static final String TOKEN = "token";

	public static boolean isLogin=false;

	/**SESSION USER*/
	public static final String SESSION_USER = "admin_user";

	/** key前缀 */
	public static final String KEY_LOGIN = "login_";



	public static User getCurrentUser(RedisService redisService) throws Exception{
		HttpServletRequest req = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
		String token = req.getHeader(TOKEN);
		User user = (User)redisService.get(token);
		if(null == user){
			throw new InterfaceCommonException(StatusConstant.NOTLOGIN,"未登录");
		}
		if(StatusConstant.NO.equals(user.getStatus())){
			throw new InterfaceCommonException(StatusConstant.ACCOUNT_FROZEN,"帐号无效");
		}
		return user;
	}

}
