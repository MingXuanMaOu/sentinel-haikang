package com.ming.controller;

import com.ming.service.QvsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author liuming
 * @description
 * @date 2023/1/17
 */
@RestController
@RequestMapping("radar")
public class StreamController {

    @Autowired
    QvsService qvsService;

    @GetMapping("/stream")
    public String stream(HttpServletRequest request){
        String str = qvsService.getStreamUrl("31011500991320021475");
        System.out.println("得到的：" + str);
        return str;
    }

}
