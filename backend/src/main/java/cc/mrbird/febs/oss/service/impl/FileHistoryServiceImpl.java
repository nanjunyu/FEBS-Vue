package cc.mrbird.febs.oss.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.RedisConnectException;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void setPresent(Long id, User user) throws Exception {

        /**
         * ① 找出之前的主版本数据将其改成废弃
         * ② 通过id在附表找到需要设置为主版本的数据，复制新增一条，并且把状态改为主版本，
         * ③ 替换主表里的状态
         */
        FileHistory fileHistory = fileHistoryMapper.selectById(id);
        LocalDateTime now = LocalDateTime.now();
        if (fileHistory != null) {
            fileHistory.setId(null);
            fileHistory.setUpdateTime(now);
            fileHistory.setAuthor(user.getAvatar());
            fileHistory.setStatus(OssConstant.FILE_STATUS_CURRENT);
            LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FileHistory::getParentId, fileHistory.getParentId());
            List<FileHistory> fileHistoryList = fileHistoryMapper.selectList(queryWrapper);
            if (!fileHistoryList.isEmpty()) {
                for (FileHistory file : fileHistoryList) {
                    if (OssConstant.FILE_STATUS_CURRENT.equals(file.getStatus())) {
                        file.setStatus(OssConstant.FILE_STATUS_OUT);
                        fileHistoryMapper.updateById(file);
                        cacheService.deleteFastDfsUrl(file.getFileId());
                    }
                }
            }

            fileHistoryMapper.insert(fileHistory);
            fileCurrentMapper.updateById(fileHistory);
            /**
             * 修改老数据id 对应的url
             * 新增  新的id对应的url
             */
            updateFastDfsUrl(fileHistory.getParentId(), fileHistory.getFastPath());
        }
    }


    @Override
    public IPage<?> findSysInfoPageByParentId(String parentId, String userId, QueryRequest queryRequest) {
        try {
            LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
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

    @Override
    public String findMasterFastUrlByFileId(String fileId) throws Exception {
        String url = cacheService.getFastDfsUrlByFileId(fileId);
        if (!StringUtils.isEmpty(url)) {
            return url;
        } else {
            LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FileHistory::getFileId, fileId);
            List<FileHistory> fileHistoryList = fileHistoryMapper.selectList(queryWrapper);
            if (!fileHistoryList.isEmpty()) {
                FileHistory fileHistory = fileHistoryList.get(0);
                Long parentId = fileHistory.getParentId();
                FileCurrent fileCurrent = fileCurrentMapper.selectById(parentId);
                if (fileCurrent != null && OssConstant.FILE_STATUS_CURRENT.equals(fileCurrent.getStatus())) {
                    url = fileCurrent.getFastPath();
                    cacheService.saveFastDfsUrl(fileId, url);
                } else {
                    log.error("访问的文件没有对应的在用版本!.....fileId=" + fileId);
                }
            }
        }
        return url;
    }

    @Override
    public Long increaseFilePv(String fileId) throws Exception {
        String oldPv = cacheService.getFastDfsPvByFileId(fileId);
        if (StringUtils.isEmpty(oldPv)) {
            return cacheService.setFastDfsPv(fileId, "1");
        } else {
            return cacheService.setFastDfsPv(fileId, String.valueOf((Integer.parseInt(oldPv) + 1)));
        }
    }

    @Override
    public void getAllFastPv() throws RedisConnectException {
        Map<String, String> map = cacheService.getAllFastPv();
        if (!map.isEmpty()) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(FileHistory::getFileId, key);
                List<FileHistory> fileHistoryList = fileHistoryMapper.selectList(queryWrapper);
                if (!fileHistoryList.isEmpty()) {
                    for (FileHistory fileHistory : fileHistoryList) {
                        fileHistory.setPv(fileHistory.getPv() + Integer.parseInt(map.get(key)));
                        this.baseMapper.updateById(fileHistory);
                        cacheService.deleteFastDfsPv(key);
                    }
                }
            }
            LambdaQueryWrapper<FileCurrent> queryWrapper = new LambdaQueryWrapper<>();
            List<FileCurrent> fileCurrentList = fileCurrentMapper.selectList(queryWrapper);
            if (!fileCurrentList.isEmpty()) {
                for (FileCurrent fileCurrent : fileCurrentList) {
                    Integer pv = fileHistoryMapper.sumPvByParentId(fileCurrent.getId());
                    fileCurrent.setPv(pv);
                    fileCurrentMapper.updateById(fileCurrent);
                }
            }
        }
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
        if (file != null) {
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
            fileHistory.setAuthor(user.getUsername());
            uploadInfo.setFileName(file.getOriginalFilename());
            uploadInfo.setFileSize(file.getSize());
            uploadInfo.setCreateTime(now);
            uploadInfo.setUrl(fastDfsConstant.trackerServer + storePath.getFullPath());
            fileCurrentMapper.insert(fileHistory);
            fileHistory.setParentId(fileHistory.getId());
            fileHistory.setId(null);
            fileHistoryMapper.insert(fileHistory);
            /**
             * 如果设置为了当前版本 ，那么需要将id对应的url存到redis中
             */
            if (OssConstant.FILE_STATUS_CURRENT.equals(uploadVo.getStatus())) {
                cacheService.saveFastDfsUrl(fileId, storePath.getFullPath());
            }
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
            //LambdaQueryWrapper<FileCurrent> fileCurrentLambdaWrapper = new LambdaQueryWrapper<>();
            // 如果重新上传了文件 需要调用fastDfs上传
            if (file != null) {
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

            }
            fileHistory.setVersionNumber(new BigDecimal(uploadVo.getVersionNumber()));
            fileHistory.setStatus(uploadVo.getStatus());
            fileHistory.setFileName(uploadVo.getFileName());
            fileHistory.setAuthor(user.getUsername());
            fileHistory.setUpdateTime(now);
            fileHistory.setId(null);
            fileHistoryMapper.insert(fileHistory);
            //如果修改的时候选了当前在用，就把上一个变成废弃
            if (OssConstant.FILE_STATUS_CURRENT.equals(uploadVo.getStatus())) {
                FileHistory oldFile = new FileHistory();
                oldFile.setStatus(OssConstant.FILE_STATUS_OUT);
                oldFile.setId(id);
                fileHistoryMapper.updateById(oldFile);
            }
            FileCurrent fileCurrent = new FileCurrent();
            BeanUtils.copyProperties(fileHistory, fileCurrent);
            fileCurrent.setId(parentId);
            fileCurrentMapper.updateById(fileCurrent);
            /**
             * 如果重新上传了文件并且，设置为了当前版本 ，那么需要修改一下redis中id对应的url地址,否则的话不存到redis中，不提供访问
             */
            if (file != null && OssConstant.FILE_STATUS_CURRENT.equals(uploadVo.getStatus())) {
                /**
                 * 修改老数据id 对应的url
                 * 新增  新的id对应的url
                 */
                updateFastDfsUrl(id, fileHistory.getFastPath());
                cacheService.saveFastDfsUrl(fileCurrent.getFileId(), fileCurrent.getFastPath());
            }


        }
        return uploadInfo;
    }


    /**
     * 同步修改fastDfs缓存信息
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/27 15:09
     */
    private void updateFastDfsUrl(Long parentId, String fastPath) throws Exception {
        LambdaQueryWrapper<FileHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileHistory::getParentId, parentId);
        List<FileHistory> fileHistoryList = fileHistoryMapper.selectList(queryWrapper);
        if (!fileHistoryList.isEmpty()) {
            for (FileHistory fileHistory : fileHistoryList) {
                String fileId = fileHistory.getFileId();
                cacheService.deleteFastDfsUrl(fileId);
                cacheService.saveFastDfsUrl(fileId, fastPath);
            }
        }
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
            //  LambdaQueryWrapper<FileCurrent> fileCurrentLambdaWrapper = new LambdaQueryWrapper<>();
            // 如果重新上传了文件 需要调用fastDfs上传
            if (file != null) {
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

            }
            fileCurrent.setVersionNumber(new BigDecimal(uploadVo.getVersionNumber()));
            fileCurrent.setStatus(uploadVo.getStatus());
            fileCurrent.setFileName(uploadVo.getFileName());
            fileCurrent.setAuthor(user.getUsername());
            fileCurrent.setUpdateTime(now);
            FileHistory fileHistory = new FileHistory();
            BeanUtils.copyProperties(fileCurrent, fileHistory);
            fileCurrentMapper.updateById(fileCurrent);
            fileHistory.setId(null);
            fileHistory.setParentId(id);
            fileHistoryMapper.insert(fileHistory);
            /**
             * 如果重新上传了文件并且，设置为了当前版本 ，那么需要修改一下redis中id对应的url地址,否则的话不存到redis中，不提供访问
             */
            if (file != null && OssConstant.FILE_STATUS_CURRENT.equals(uploadVo.getStatus())) {
                /**
                 * 修改老数据id 对应的url
                 * 新增  新的id对应的url
                 */
                updateFastDfsUrl(id, fileHistory.getFastPath());
                cacheService.saveFastDfsUrl(fileCurrent.getFileId(), fileCurrent.getFastPath());
            }
        }
        return uploadInfo;
    }
}
