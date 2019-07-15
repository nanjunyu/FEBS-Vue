package cc.mrbird.febs.oss.controller;


import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.fastdfs.FastDfsConstant;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.oss.dao.FileHistoryMapper;
import cc.mrbird.febs.oss.domain.FileHistory;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UploadVo;
import cc.mrbird.febs.oss.service.FileCurrentService;
import cc.mrbird.febs.oss.service.FileHistoryService;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Frank
 */
@Slf4j
@Validated
@RestController
@Api(tags = "附件相关API")
@RequestMapping("/oss")
public class FileController extends BaseController {

    @Autowired
    FileHistoryService fileHistoryService;

    @Autowired
    FileCurrentService fileCurrentService;

    private String message;

    @Autowired
    FileHistoryMapper fileHistoryMapper;

    @Autowired
    FastDfsConstant fastDfsConstant;

    /**
     * @Author: nanJunYu
     * @Description:上传附件
     * @Date: Create in  2018/8/1 9:18
     * @params:
     * @return:
     */
    @ApiOperation(value = "新增文件", notes = "新增文件")
    @PostMapping("upload")
    @RequiresPermissions("oss:add")
    public UploadInfo upload(@Valid UploadVo uploadVo, @ApiParam(name = "file", required = true, value = "附件对象") MultipartFile file, HttpServletRequest request) throws FebsException {
        UploadInfo uploadInfo = null;
        try {
            User user = FebsUtil.getCurrentUser();
            uploadInfo = fileHistoryService.uploadFile(file, uploadVo, user);
        } catch (Exception e) {
            message = "上传附件失败";
            log.error(message, e);
            throw new FebsException(message);
        }
        return uploadInfo;

    }

    /**
     * 获取子文件详情
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/10 14:06
     */
    @ApiOperation(value = "获取子文件详情", notes = "获取子文件详情")
    @GetMapping("getInfoByParentId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父级id", required = true, paramType = "query", dataType = "String")
    })
    public Map<String, Object> getSysInfoPageByParentId(QueryRequest queryRequest, @ApiParam(name = "parentId", required = true, value = "父级id") String parentId) {
        User user = FebsUtil.getCurrentUser();
        return getDataTable(fileHistoryService.findSysInfoPageByParentId(parentId, String.valueOf(user.getUserId()), queryRequest));
    }


    /**
     * 检查文件是否存在
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/10 15:37
     */
    @ApiOperation(value = "检查文件是否存在", notes = "检查文件是否存在")
    @GetMapping("check")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, paramType = "query", dataType = "String")
    })
    public boolean checkPassword(@ApiParam(name = "fileName", required = true, value = "文件名称") String fileName
    ) {
        LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileHistory::getFileName, fileName);
        List<FileHistory> fileHistoryList = fileHistoryService.getBaseMapper().selectList(queryWrapper);
        if (fileHistoryList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设为在用版本
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/10 15:36
     */
    @ApiOperation(value = "设为在用版本", notes = "设为在用版本")
    @PostMapping("setPresent")
    @RequiresPermissions("oss:setCurrent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "String")
    })
    public boolean setPresent(@ApiParam(name = "id", required = true, value = "id") String id) throws FebsException {
        User user = FebsUtil.getCurrentUser();
        try {
            fileHistoryService.setPresent(Long.valueOf(id), user);
        } catch (Exception e) {
            message = "设置为主版本失败";
            log.error(message, e);
            throw new FebsException(message);
        }
        return true;
    }


    /**
     * 根据附件id获取详情
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/10 15:47
     */
    @ApiOperation(value = "根据附件id获取详情", notes = "根据附件id获取详情")
    @GetMapping("getById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "String")
    })
    public Map<String, Object> getById(@ApiParam(name = "id", required = true, value = "id") String id) {
        Map<String, Object> retMap = new HashMap<>(16);
        FileHistory fileHistory = fileHistoryService.getBaseMapper().selectById(id);
        Long parentId = fileHistory.getParentId();
        retMap.put("data", fileHistory);
        retMap.put("maxVersion", fileHistoryMapper.getMaxVersion(parentId).getVersionNumber());
        return retMap;
    }


    /**
     * 主列表数据
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/11 9:55
     */
    @ApiOperation(value = "主文件列表", notes = "主文件列表")
    @GetMapping("page")
    @RequiresPermissions("oss:view")
    public Map<String, Object> getSysInfoPage(QueryRequest queryRequest) {
        User user = FebsUtil.getCurrentUser();
        return getDataTable(fileCurrentService.findFileCurrentPage(user, queryRequest));
    }

    /**
     * 主列表数据
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/11 9:55
     */
    @ApiOperation(value = "管理员主文件列表", notes = "管理员主文件列表")
    @GetMapping("adminPage")
    @RequiresPermissions("ossAdmin:view")
    public Map<String, Object> getAdminSysInfoPage(QueryRequest queryRequest) {
        return getDataTable(fileCurrentService.findFileCurrentPage(queryRequest));
    }

    /**
     * 修改文件
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/11 10:36
     */
    @ApiOperation(value = "修改文件", notes = "修改文件")
    @RequiresPermissions("oss:update")
    @PutMapping("updateFile")
    public UploadInfo updateFile(@Valid UploadVo uploadVo, @ApiParam(name = "file", value = "附件对象") MultipartFile file, HttpServletRequest request) throws FebsException {
        UploadInfo uploadInfo = null;
        try {
            User user = FebsUtil.getCurrentUser();
            if (!StringUtils.isEmpty(uploadVo.getParentId())) {
                //修改子版本
                uploadInfo = fileHistoryService.uploadFile(Long.parseLong(uploadVo.getId()), Long.parseLong(uploadVo.getParentId()), file, uploadVo, user);
            } else {
                //修改主版本
                uploadInfo = fileHistoryService.uploadFile(Long.parseLong(uploadVo.getId()), file, uploadVo, user);
            }

        } catch (Exception e) {
            message = "修改附件失败";
            log.error(message, e);
            throw new FebsException(message);
        }
        return uploadInfo;
    }


    /**
     * 拦截访问过来的文件 进行逻辑处理 重定向
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/21 17:26
     */
    @GetMapping("getUrl")
    public void getUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String oldUrl = request.getParameter("oldUrl");
        String uuid= String.valueOf(UUID.randomUUID());
        log.error("oldUrl"+oldUrl);
        if (!StringUtils.isEmpty(oldUrl)) {
            String[] oldStr = oldUrl.split("/");
            String fileId = oldStr[oldStr.length - 1];
            if (!StringUtils.isEmpty(fileId)) {
                try {
                    String url = fileHistoryService.findMasterFastUrlByFileId(fileId);
                    if (!StringUtils.isEmpty(url)) {
                        fileHistoryService.increaseFilePv(fileId);
                        response.sendRedirect(fastDfsConstant.trackerServer+url+"?token="+uuid);
                    }else{
                        response.sendRedirect(fastDfsConstant.trackerServer+oldUrl);
                    }
                } catch (Exception e) {
                    message = "重定向失败";
                    log.error(message, e);
                    response.sendRedirect(fastDfsConstant.trackerServer+oldUrl);
                }

            }

        }else{
            log.error("这是一个有问题的请求:原始请求地址为:"+request.getQueryString());
        }

    }

}
