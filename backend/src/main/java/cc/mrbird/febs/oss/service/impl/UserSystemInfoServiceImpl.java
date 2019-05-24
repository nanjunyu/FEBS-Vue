package cc.mrbird.febs.oss.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.fastdfs.FastDfsUtils;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.oss.dao.UserSystemInfoMapper;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UserSystemInfo;
import cc.mrbird.febs.oss.service.UserSystemInfoService;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author Frank
 */
@Slf4j
@Service("userSystemInfoService")
public class UserSystemInfoServiceImpl extends ServiceImpl<UserSystemInfoMapper, UserSystemInfo> implements UserSystemInfoService {
    private String message;

    @Autowired
    DefaultGenerateStorageClient defaultGenerateStorageClient;

    @Override
    public UploadInfo uploadFile(MultipartFile file, UserSystemInfo userSystemInfo, HttpServletRequest request) throws FebsException {
        UploadInfo uploadInfo = new UploadInfo();
        try {
            if (!file.isEmpty()) {
                Date createTime = new Date();
             /*   // 构建上传文件的存放路径
                String path = request.getServletContext().getRealPath("/upload/");
                System.out.println("path = " + path);

                // 获取上传的文件名称，并结合存放路径，构建新的文件名称
                String filename = file.getOriginalFilename();
                File filepath = new File(path, filename);

                // 判断路径是否存在，不存在则新创建一个
                if (!filepath.getParentFile().exists()) {
                    filepath.getParentFile().mkdirs();
                }

                String filePath=path + File.separator + filename;
                // 将上传文件保存到目标文件目录
                file.transferTo(new File(filePath));

                String md5= DigestUtils.md5Hex(new FileInputStream(filePath));
                System.out.println(md5);*/
                // 获取上传的文件名称，并结合存放路径，构建新的文件名称
                String filename = userSystemInfo.getFileName();
                LambdaQueryWrapper<UserSystemInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserSystemInfo::getFileName, filename);
                List<UserSystemInfo> userSystemInfoList= baseMapper.selectList(queryWrapper);
                if(!userSystemInfoList.isEmpty()){
                    uploadInfo.setCode(-2);
                    return uploadInfo;
                }
                String _fileName=file.getOriginalFilename();
                StorePath storePath = defaultGenerateStorageClient.uploadFile("group1", file.getInputStream(), file.getSize(), _fileName.substring(_fileName.lastIndexOf(".") + 1));
                System.out.println(storePath);
                String fileIds[] = storePath.getPath().split("/");
                String fileId = fileIds[fileIds.length - 1];
              /*  UserSystemInfo userSystemInfo=new UserSystemInfo();*/
                uploadInfo.setFileName(file.getOriginalFilename());
                uploadInfo.setFileSize(file.getSize());
                uploadInfo.setCreateTime(createTime);
               /* FastDfsUtils fastDfsUtils=new FastDfsUtils();*/
                uploadInfo.setUrl(FastDfsUtils.trackerServer + storePath.getFullPath());
               /* baseMapper.selectObjs();*/
                return  uploadInfo;
            }
          /*  String userBucketName = uploadVo.getBucket();
            Integer userId = user.getId();
            UserBuckets userBuckets = getUserBucketByuIdAndName(userBucketName, userId);
            if (userBuckets != null) {
                Date createTime = new Date();
                if (!file.isEmpty()) {
                    UserBucketInfo userBucketInfo = new UserBucketInfo();
                    StorePath storePath = fastDFSClientUtil.uploadFile(file);
                    userBucketInfo.setCreateTime(createTime);
                    userBucketInfo.setFileName(file.getOriginalFilename());
                    userBucketInfo.setFileSize(file.getSize());
                    userBucketInfo.setUserBucketsId(userBuckets.getId());
                    userBucketInfo.setFastPath(storePath.getFullPath());
                    userBucketInfo.setUploadPath(uploadVo.getBucket());
                    userBucketInfo.setUserId(userId);
                    userBucketInfo.setStatus(ossConstant.getUsableStatus());
                    String fileIds[] = storePath.getPath().split("/");
                    String fileId = fileIds[fileIds.length - 1];
                    userBucketInfo.setFileId(fileId);
                    userBucketInfoService.createUserBucketInfoById(userBucketInfo);
                    uploadInfo.setFileName(file.getOriginalFilename());
                    uploadInfo.setFileSize(file.getSize());
                    uploadInfo.setCreateTime(createTime);
                    uploadInfo.setUrl(trackerServer + storePath.getFullPath());
                }*/

        } catch (IOException e) {
            message = "上传附件失败";
            log.error(message, e);
            throw new FebsException(message);
        }
        return uploadInfo;
    }

    @Override
    public IPage<UserSystemInfo> findSysInfoPage(User user, QueryRequest request) {
        try {
            LambdaQueryWrapper<UserSystemInfo> queryWrapper = new LambdaQueryWrapper<>();

            if (user.getUserId()!=null) {
                queryWrapper.eq(UserSystemInfo::getUserId, user.getUserId());
            }
           /* if (StringUtils.isNotBlank(role.getCreateTimeFrom()) && StringUtils.isNotBlank(role.getCreateTimeTo())) {
                queryWrapper
                        .ge(Role::getCreateTime, role.getCreateTimeFrom())
                        .le(Role::getCreateTime, role.getCreateTimeTo());
            }*/
            Page<UserSystemInfo> page = new Page<>();
            SortUtil.handlePageSort(request, page, true);
            return this.page(page,queryWrapper);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            return null;
        }
    }
}
