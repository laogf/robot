package com.robot.robot.controller.app;


import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.robot.common.utils.RequestUtil;
import com.robot.robot.controller.app.bean.ResponseBean;
import com.robot.robot.domain.TAppSoftwareDO;
import com.robot.robot.service.TAppSoftwareService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * apk升级版本管理
 */
@RestController
@RequestMapping("/app/software")
public class AppSoftwareController {
	@Autowired
	private TAppSoftwareService tAppSoftwareService;
    
    private Logger logger = LoggerFactory.getLogger(AppSoftwareController.class);

    /**
     * 获取最新的apk下载包
     */
    @RequestMapping(value = "/upgrade",method= RequestMethod.GET)
    public ResponseBean upgrade(HttpServletRequest request) {
    	String robotNo=RequestUtil.getString(request, "robotNo");
        Map<String,Object> map =new HashMap<String,Object>();
        try {
        	TAppSoftwareDO appSoft = tAppSoftwareService.getNewAPK(map);
        	List<TAppSoftwareDO> appSoftList = new ArrayList<>();
        	if(appSoft != null){
        		appSoftList.add(appSoft);
        	}
            return ResponseBean.success(appSoftList);
        }catch (Exception e) {
            logger.error("robot："+robotNo+ " upgrade error", e);
        }
        return ResponseBean.fail();
    }
}
