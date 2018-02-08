package com.magicbeans.happygo.controller;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.SendRequestUtil;
import com.magicbeans.happygo.util.StatusConstant;
import com.magicbeans.happygo.util.WeChatConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Eric Xie on 2017/4/6 0006.
 */
@RequestMapping("/wxClient")
@Controller
@Api(value = "/微信客户端,JS API签名接口 ",description = "仅限微信客户端使用")
public class WXClientController extends BaseController {


    /**
     *  微信客户端 获取 签名接口
     * @return
     */
    @RequestMapping(value = "/jsSign",method = RequestMethod.POST)
    @ApiOperation(value = "微信客户端 获取 签名接口",notes = "仅限微信客户端使用")
    @ApiImplicitParam(name = "url",value = "待签名的URL",paramType ="string" ,dataType = "String.class")
    public @ResponseBody
    ResponseData wxSign(String url, HttpServletRequest request){
        try {
            HttpSession session = request.getSession();
            String accessToken = (String)session.getAttribute(WeChatConfig.ACCESS_TOKEN);
            if(CommonUtil.isEmpty(accessToken)){
                String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WeChatConfig.getValue("appId") + "&secret=" + WeChatConfig.getValue("secret");
                String resultStr = SendRequestUtil.sendRequest(requestUrl,"GET");
                logger.info("微信客户端获取 toke :"+resultStr);
                JSONObject jsonObject = JSONObject.fromObject(resultStr);
                accessToken = jsonObject.getString("access_token");
            }

            String ticketStr = (String)session.getAttribute(WeChatConfig.TICKET);

            if(CommonUtil.isEmpty(ticketStr)){
                String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
                String ticketResult = SendRequestUtil.sendRequest(jsapiTicketUrl,"GET");
                logger.info("微信客户端获取 ticketResult :"+ticketResult);
                JSONObject object = JSONObject.fromObject(ticketResult);
                ticketStr = object.getString("ticket");
            }

            // 缓存数据到 session
            session.setAttribute(WeChatConfig.ACCESS_TOKEN,accessToken);
            session.setAttribute(WeChatConfig.TICKET,ticketStr);

            Map<String,Object> data = new TreeMap<String, Object>();
            data.put("jsapi_ticket",ticketStr);
            data.put("noncestr", UUID.randomUUID().toString().replaceAll("-", ""));
            data.put("timestamp",System.currentTimeMillis() / 1000);
            data.put("url",url);
            data = wxSign(data); // 代签名
            data.put("appId",WeChatConfig.getValue("appId"));
             data.put("access_token",accessToken);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE, "获取成功",data);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
    }





    public static Map<String, Object> wxSign(Map<String, Object> param) {
        String ret = "";
        String signature = "";
        for (String key : param.keySet()) {
            if (key.equals("sign")) {
                continue;
            } else {
                ret += key + "=" + param.get(key) + "&";
            }
        }
        ret = ret.substring(0,ret.length() - 1);
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(ret.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        param.put("signature", signature);
        return param;
    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }



}
