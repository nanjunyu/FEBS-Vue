package cc.mrbird.febs.oss.controller;


import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UserSystemInfo;
import cc.mrbird.febs.oss.service.UserSystemInfoService;
import cc.mrbird.febs.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author Frank
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/oss")
public class UserSystemInfoController extends BaseController {

    @Autowired
    UserSystemInfoService userSystemInfoService;

    private String message;

    /**
     * @Author: nanJunYu
     * @Description:上传附件
     * @Date: Create in  2018/8/1 9:18
     * @params:
     * @return:
     */
    @PutMapping("upload")
    public UploadInfo upload(@Valid UserSystemInfo userSystemInfo, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws FebsException {
        UploadInfo uploadInfo = null;
        try {
            uploadInfo = userSystemInfoService.uploadFile(file,userSystemInfo,request);
        } catch (Exception e) {
            message = "上传附件失败";
            log.error(message, e);
            throw new FebsException(message);
        }
        return uploadInfo;

    }


    @GetMapping("page")
    public Map<String, Object> getSysInfoPage(QueryRequest queryRequest, User user){
       return getDataTable(userSystemInfoService.findSysInfoPage(user,queryRequest));
    }
}
