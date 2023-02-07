package com.ming.service;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.http.Response;
import com.qiniu.qvs.DeviceManager;
import com.qiniu.qvs.NameSpaceManager;
import com.qiniu.qvs.PTZManager;
import com.qiniu.qvs.StreamManager;
import com.qiniu.qvs.model.DynamicLiveRoute;
import com.qiniu.util.Auth;
import lombok.SneakyThrows;
import net.minidev.json.JSONUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author liuming
 * @description
 * @date 2023/1/17
 */
@Component
public class QvsService {

    Auth auth;
    DeviceManager deviceManager;
    StreamManager streamManager;
    PTZManager ptzManager;


    @PostConstruct
    public void init(){
        this.auth = Auth.create("m1mVfNXbUqe8MCfnJXAJPd8aEwVMIB_MK5v5BnYJ","Yy33aT0AD_g--rqiKPWzvytLvoOXPwDbIAMJAWa7");
        this.deviceManager = new DeviceManager(auth);

        this.streamManager = new StreamManager(auth);
    }

    @SneakyThrows
    public String getStreamUrl(String channelId){
//        deviceManager.startDevice("gbceshi",channelId,new String[]{channelId});
//        Response response = streamManager.dynamicPublishPlayURL("gbceshi", channelId, new DynamicLiveRoute("223.112.103.38", "223.112.103.38", 5061));
        Response response = deviceManager.queryGBRecordHistories("gbceshi", channelId, null, 1674003600, 1674004800);
        return response.bodyString();
    }
}
