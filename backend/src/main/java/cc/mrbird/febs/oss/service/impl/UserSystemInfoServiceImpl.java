package cc.mrbird.febs.oss.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.fastdfs.FastDfsUtils;
import cc.mrbird.febs.common.service.CacheService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Autowired
    CacheService cacheService;

    @Override
    public UploadInfo uploadFile(MultipartFile file, UserSystemInfo userSystemInfo, HttpServletRequest request) throws Exception {
        UploadInfo uploadInfo = new UploadInfo();
        if (!file.isEmpty()) {
            Date createTime = new Date();
            // 获取上传的文件名称，并结合存放路径，构建新的文件名称
            String filename = userSystemInfo.getFileName();
            if (userSystemInfo.getConfirm() != 1) {
                LambdaQueryWrapper<UserSystemInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserSystemInfo::getFileName, filename);
                List<UserSystemInfo> userSystemInfoList = baseMapper.selectList(queryWrapper);
                if (!userSystemInfoList.isEmpty()) {
                    uploadInfo.setCode(-2);
                    return uploadInfo;
                }
            }
                String _fileName = file.getOriginalFilename();
                StorePath storePath = defaultGenerateStorageClient.uploadFile("group1", file.getInputStream(), file.getSize(), _fileName.substring(_fileName.lastIndexOf(".") + 1));
                System.out.println(storePath);
                UserSystemInfo userSystemInfo1 = new UserSystemInfo();
                LocalDateTime now = LocalDateTime.now();
                userSystemInfo1.setCreateTime(now);
                userSystemInfo1.setMimeType(file.getContentType());
                userSystemInfo1.setPv(0);
                userSystemInfo1.setStatus(userSystemInfo.getStatus());
                userSystemInfo1.setFastPath(storePath.getFullPath());
                String suffix = _fileName.substring(_fileName.lastIndexOf(".") + 1);
                userSystemInfo1.setSuffix(suffix);
                String fileIds[] = storePath.getPath().split("/");
                String fileId = fileIds[fileIds.length - 1];
                userSystemInfo1.setFileId(fileId);
                userSystemInfo1.setFileName(filename);
                userSystemInfo1.setFileSize(new BigDecimal(file.getSize()));
                userSystemInfo1.setUserId(userSystemInfo.getUserId());
                this.saveUpload(userSystemInfo1);
                uploadInfo.setFileName(file.getOriginalFilename());
                uploadInfo.setFileSize(file.getSize());
                uploadInfo.setCreateTime(createTime);
                uploadInfo.setUrl(FastDfsUtils.trackerServer + storePath.getFullPath());
                return uploadInfo;
        }
        return uploadInfo;
    }

    @Override
    public IPage<UserSystemInfo> findSysInfoPage(User user, QueryRequest request) {
        try {
            LambdaQueryWrapper<UserSystemInfo> queryWrapper = new LambdaQueryWrapper<>();

            if (user.getUserId() != null) {
                queryWrapper.eq(UserSystemInfo::getUserId, user.getUserId());
            }
            Page<UserSystemInfo> page = new Page<>();
            SortUtil.handlePageSort(request, page, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            return null;
        }
    }

    @Override
    public void saveUpload(UserSystemInfo userSystemInfo) throws Exception {
        baseMapper.insert(userSystemInfo);
        cacheService.saveUpload(userSystemInfo);
    }

}
