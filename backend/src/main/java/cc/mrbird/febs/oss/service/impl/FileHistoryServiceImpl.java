package cc.mrbird.febs.oss.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.fastdfs.FastDfsConstant;
import cc.mrbird.febs.common.service.CacheService;
import cc.mrbird.febs.common.utils.SortUtil;
import cc.mrbird.febs.oss.common.OssConstant;
import cc.mrbird.febs.oss.dao.FileCurrentMapper;
import cc.mrbird.febs.oss.dao.FileHistoryMapper;
import cc.mrbird.febs.oss.domain.FileCurrent;
import cc.mrbird.febs.oss.domain.FileHistory;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UploadVo;
import cc.mrbird.febs.oss.service.FileHistoryService;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Frank
 */
@Slf4j
@Service("fileHistoryService")
public class FileHistoryServiceImpl extends ServiceImpl<FileHistoryMapper, FileHistory> implements FileHistoryService {
    private String message;

    @Autowired
    DefaultGenerateStorageClient defaultGenerateStorageClient;

    @Autowired
    CacheService cacheService;

    @Autowired
    FileHistoryMapper fileHistoryMapper;

    @Autowired
    FileCurrentMapper fileCurrentMapper;

    @Autowired
    FastDfsConstant fastDfsConstant;


    @Override
    public void setPresent(Long id, User user) {

        /**
         * 通过id在附表找到需要设置为主版本的数据，复制新增一条，并且把状态改为主版本，并且替换主表里的状态
         */
        FileHistory fileHistory = fileHistoryMapper.selectById(id);
        LocalDateTime now = LocalDateTime.now();
        if (fileHistory != null) {
            fileHistory.setId(null);
            fileHistory.setUpdateTime(now);
            fileHistory.setAuthor(user.getAvatar());
            fileHistory.setStatus(OssConstant.FILE_STATUS_CURRENT);
            fileHistoryMapper.insert(fileHistory);
            LambdaQueryWrapper<FileCurrent> fileCurrentLambdaWrapper = new LambdaQueryWrapper<>();
            fileCurrentLambdaWrapper.eq(FileCurrent::getId, fileHistory.getParentId());
            fileCurrentMapper.update(fileHistory, fileCurrentLambdaWrapper);
        }
    }

    @Override
    public void saveUpload(FileHistory fileHistory) throws Exception {
        fileCurrentMapper.insert(fileHistory);
        System.out.println(fileHistory.getId());
        fileHistory.setParentId(fileHistory.getId());
        fileHistoryMapper.insert(fileHistory);
        cacheService.saveUpload(fileHistory);
    }

    @Override
    public IPage<?> findSysInfoPageByParentId(String parentId, String userId, QueryRequest queryRequest) {
        try {
            LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();

            if (userId != null) {
                queryWrapper.eq(FileHistory::getUserId, userId);
            }
            if (parentId != null) {
                queryWrapper.eq(FileHistory::getParentId, parentId);
            }
            Page<FileHistory> page = new Page<>();
            SortUtil.handlePageSort(queryRequest, page, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取详情失败", e);
            return null;
        }
    }


    /**
     * 修改子版本
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 16:27
     */
    @Override
    public UploadInfo uploadFile(Long id, Long parentId, MultipartFile file, UploadVo uploadVo, User user) throws Exception {
        return SaveUpload(file, uploadVo, user, id, parentId);
    }


    /**
     * 修改主版本
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 16:27
     */
    @Override
    public UploadInfo uploadFile(Long id, MultipartFile file, UploadVo uploadVo, User user) throws Exception {
        return SaveUpload(file, uploadVo, user, id);
    }

    /**
     * 新增的情况
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 16:09
     */
    @Override
    public UploadInfo uploadFile(MultipartFile file, UploadVo uploadVo, User user) throws Exception {
        return SaveUpload(file, uploadVo, user);
    }


    /**
     * 新增的情况
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 15:12
     */
    public UploadInfo SaveUpload(MultipartFile file, UploadVo uploadVo, User user) throws Exception {
        UploadInfo uploadInfo = new UploadInfo();
        if (!file.isEmpty()) {
            FileHistory fileHistory = new FileHistory();
            // 获取上传的文件名称，并结合存放路径，构建新的文件名称
            String filename = uploadVo.getFileName();
            String _fileName = file.getOriginalFilename();
            StorePath storePath = defaultGenerateStorageClient.uploadFile("group1", file.getInputStream(), file.getSize(), _fileName.substring(_fileName.lastIndexOf(".") + 1));
            System.out.println(storePath);
            LocalDateTime now = LocalDateTime.now();
            fileHistory.setCreateTime(now);
            fileHistory.setMimeType(file.getContentType());
            fileHistory.setPv(0);
            fileHistory.setStatus(uploadVo.getStatus());
            fileHistory.setVersionNumber(new BigDecimal(uploadVo.getVersionNumber()));
            fileHistory.setFastPath(storePath.getFullPath());
            String suffix = _fileName.substring(_fileName.lastIndexOf(".") + 1);
            fileHistory.setSuffix(suffix);
            String fileIds[] = storePath.getPath().split("/");
            String fileId = fileIds[fileIds.length - 1];
            fileHistory.setFileId(fileId);
            fileHistory.setFileName(filename);
            fileHistory.setFileSize(new BigDecimal(file.getSize()));
            fileHistory.setUserId(user.getUserId());
            fileHistory.setAuthor(user.getAvatar());
            uploadInfo.setFileName(file.getOriginalFilename());
            uploadInfo.setFileSize(file.getSize());
            uploadInfo.setCreateTime(now);
            uploadInfo.setUrl(fastDfsConstant.trackerServer + storePath.getFullPath());
            fileCurrentMapper.insert(fileHistory);
            fileHistory.setParentId(fileHistory.getId());
            fileHistory.setId(null);
            fileHistoryMapper.insert(fileHistory);
            cacheService.saveUpload(fileHistory);
        }
        return uploadInfo;
    }


    /**
     * 修改子版本
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 15:12
     */
    public UploadInfo SaveUpload(MultipartFile file, UploadVo uploadVo, User user, Long id, Long parentId) throws Exception {
        UploadInfo uploadInfo = new UploadInfo();
        FileHistory fileHistory = fileHistoryMapper.selectById(id);
        if (fileHistory != null) {
            LocalDateTime now = LocalDateTime.now();
            LambdaQueryWrapper<FileCurrent> fileCurrentLambdaWrapper = new LambdaQueryWrapper<>();
            // 如果重新上传了文件 需要调用fastDfs上传
            if (!file.isEmpty()) {
                String _fileName = file.getOriginalFilename();
                StorePath storePath = defaultGenerateStorageClient.uploadFile("group1", file.getInputStream(), file.getSize(), _fileName.substring(_fileName.lastIndexOf(".") + 1));
                System.out.println(storePath);
                String fileIds[] = storePath.getPath().split("/");
                String fileId = fileIds[fileIds.length - 1];
                String suffix = _fileName.substring(_fileName.lastIndexOf(".") + 1);
                fileHistory.setFileSize(new BigDecimal(file.getSize()));
                fileHistory.setMimeType(file.getContentType());
                fileHistory.setFastPath(storePath.getFullPath());
                fileHistory.setSuffix(suffix);
                fileHistory.setFileId(fileId);
                uploadInfo.setUrl(fastDfsConstant.trackerServer + storePath.getFullPath());
                uploadInfo.setFileName(file.getOriginalFilename());
                uploadInfo.setFileSize(file.getSize());
                uploadInfo.setCreateTime(now);
                fileCurrentLambdaWrapper.eq(FileCurrent::getFastPath, fileHistory.getFastPath());
                fileCurrentLambdaWrapper.eq(FileCurrent::getMd5, fileHistory.getMd5());
                fileCurrentLambdaWrapper.eq(FileCurrent::getFileId, fileHistory.getFileId());
                fileCurrentLambdaWrapper.eq(FileCurrent::getFileSize, fileHistory.getFileSize());
                fileCurrentLambdaWrapper.eq(FileCurrent::getMimeType, fileHistory.getMimeType());
                fileCurrentLambdaWrapper.eq(FileCurrent::getSuffix, fileHistory.getSuffix());

            }
            fileHistory.setVersionNumber(new BigDecimal(uploadVo.getVersionNumber()));
            fileHistory.setStatus(uploadVo.getStatus());
            fileHistory.setFileName(uploadVo.getFileName());
            fileHistory.setAuthor(user.getAvatar());
            fileHistory.setUpdateTime(now);
            fileHistory.setId(null);
            fileHistoryMapper.insert(fileHistory);
            fileCurrentLambdaWrapper.eq(FileCurrent::getStatus, fileHistory.getStatus());
            fileCurrentLambdaWrapper.eq(FileCurrent::getUpdateTime, fileHistory.getUpdateTime());
            fileCurrentLambdaWrapper.eq(FileCurrent::getId, parentId);
            fileCurrentLambdaWrapper.eq(FileCurrent::getAuthor, fileHistory.getAuthor());
            fileCurrentLambdaWrapper.eq(FileCurrent::getVersionNumber, fileHistory.getVersionNumber());
            FileCurrent fileCurrent = new FileCurrent();
            BeanUtils.copyProperties(fileHistory, fileCurrent);
            fileCurrent.setId(parentId);
            fileCurrentMapper.update(fileCurrent, fileCurrentLambdaWrapper);
            cacheService.saveUpload(fileCurrent);


        }
        return uploadInfo;
    }


    /**
     * 修改主版本的情况
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/19 15:12
     */
    public UploadInfo SaveUpload(MultipartFile file, UploadVo uploadVo, User user, Long id) throws Exception {
        UploadInfo uploadInfo = new UploadInfo();
        FileCurrent fileCurrent = fileCurrentMapper.selectById(id);
        if (fileCurrent != null) {
            LocalDateTime now = LocalDateTime.now();
            LambdaQueryWrapper<FileCurrent> fileCurrentLambdaWrapper = new LambdaQueryWrapper<>();
            // 如果重新上传了文件 需要调用fastDfs上传
            if (!file.isEmpty()) {
                String _fileName = file.getOriginalFilename();
                StorePath storePath = defaultGenerateStorageClient.uploadFile("group1", file.getInputStream(), file.getSize(), _fileName.substring(_fileName.lastIndexOf(".") + 1));
                System.out.println(storePath);
                String fileIds[] = storePath.getPath().split("/");
                String fileId = fileIds[fileIds.length - 1];
                String suffix = _fileName.substring(_fileName.lastIndexOf(".") + 1);
                fileCurrent.setFileSize(new BigDecimal(file.getSize()));
                fileCurrent.setMimeType(file.getContentType());
                fileCurrent.setFastPath(storePath.getFullPath());
                fileCurrent.setSuffix(suffix);
                fileCurrent.setFileId(fileId);
                uploadInfo.setUrl(fastDfsConstant.trackerServer + storePath.getFullPath());
                uploadInfo.setFileName(file.getOriginalFilename());
                uploadInfo.setFileSize(file.getSize());
                uploadInfo.setCreateTime(now);
                fileCurrentLambdaWrapper.eq(FileCurrent::getFastPath, fileCurrent.getFastPath());
                fileCurrentLambdaWrapper.eq(FileCurrent::getMd5, fileCurrent.getMd5());
                fileCurrentLambdaWrapper.eq(FileCurrent::getFileId, fileCurrent.getFileId());
                fileCurrentLambdaWrapper.eq(FileCurrent::getFileSize, fileCurrent.getFileSize());
                fileCurrentLambdaWrapper.eq(FileCurrent::getMimeType, fileCurrent.getMimeType());
                fileCurrentLambdaWrapper.eq(FileCurrent::getSuffix, fileCurrent.getSuffix());

            }
            fileCurrent.setVersionNumber(new BigDecimal(uploadVo.getVersionNumber()));
            fileCurrent.setStatus(uploadVo.getStatus());
            fileCurrent.setFileName(uploadVo.getFileName());
            fileCurrent.setAuthor(user.getAvatar());
            fileCurrent.setUpdateTime(now);
            fileCurrentLambdaWrapper.eq(FileCurrent::getStatus, fileCurrent.getStatus());
            fileCurrentLambdaWrapper.eq(FileCurrent::getUpdateTime, fileCurrent.getUpdateTime());
            fileCurrentLambdaWrapper.eq(FileCurrent::getId, fileCurrent);
            fileCurrentLambdaWrapper.eq(FileCurrent::getAuthor, fileCurrent.getAuthor());
            fileCurrentLambdaWrapper.eq(FileCurrent::getVersionNumber, fileCurrent.getVersionNumber());
            FileHistory fileHistory = new FileHistory();
            BeanUtils.copyProperties(fileCurrent, fileHistory);
            fileCurrentMapper.update(fileCurrent, fileCurrentLambdaWrapper);
            fileHistory.setId(null);
            fileHistory.setParentId(id);
            fileHistoryMapper.insert(fileHistory);
            cacheService.saveUpload(fileCurrent);
        }
        return uploadInfo;
    }
}
