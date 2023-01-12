package com.ming.controller;

import com.alibaba.fastjson.JSON;
import com.ming.config.Res;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author liuming
 * @description
 * @date 2023/1/12
 */
@Slf4j
@RestController
public class MinioController {
    @Autowired
    private MinioClient minioClient;

    private static final String MINIO_BUCKET = "text01";

    @GetMapping("/list")
    public List<Object> list(ModelMap map) throws Exception {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(MINIO_BUCKET)
                        .build()
        );
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
        }
        return items;
    }

    @PostMapping("/upload")
    public Res upload(@RequestParam(name = "file", required = false) MultipartFile[] file) {
        Res res = new Res();
        res.setCode(500);

        if (file == null || file.length == 0) {
            res.setMessage("上传文件不能为空");
            return res;
        }

        List<String> orgfileNameList = new ArrayList<>(file.length);

        for (MultipartFile multipartFile : file) {
            String orgfileName = multipartFile.getOriginalFilename();
            orgfileNameList.add(orgfileName);

            try {
                InputStream in = multipartFile.getInputStream();
                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(MINIO_BUCKET)
                                .object(orgfileName)
                                .stream(in, in.available(), -1)
                                .build()
                );
                in.close();
            } catch (Exception e) {
                log.error(e.getMessage());
                res.setMessage("上传失败");
                return res;
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bucketName", MINIO_BUCKET);
        data.put("fileName", orgfileNameList);
        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(data);
        return res;
    }

    @RequestMapping("/download/{fileName}")
    public void download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        InputStream in = null;
        try {
            //获取元数据
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs
                            .builder()
                            .bucket(MINIO_BUCKET)
                            .object(fileName)
                            .build());

            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            GetObjectResponse object = minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket(MINIO_BUCKET)
                            .object(fileName)
                            .build()
            );
            IOUtils.copy(object, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public Res delete(@PathVariable("fileName") String fileName) {
        Res res = new Res();
        res.setCode(200);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket("text01")
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            res.setCode(500);
            log.error(e.getMessage());
        }
        return res;
    }

    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }
}

