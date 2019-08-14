package com.invs.oss.oss.service;

import com.invs.oss.oss.domain.FileHistory;
import com.invs.oss.oss.domain.UploadInfo;
import com.invs.oss.system.domain.User;
import com.invs.oss.common.domain.QueryRequest;
import com.invs.oss.common.exception.RedisConnectException;
import com.invs.oss.oss.domain.UploadVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Frank
 */
public interface FileHistoryService extends IService<FileHistory> {

    /**
     * 将已废弃版本设为在用版本
     *
     * @param
     * @param user
     * @return
     * @Author Frank
     * @Date Create in  2019/6/18 14:28
     */
    void setPresent(Long id, User user) throws Exception;


    IPage<?> findSysInfoPageByParentId(String parentId, String userId, QueryRequest queryRequest);




    /**
     *    修改了子版本数据
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(Long id, Long ParentId, MultipartFile file, UploadVo uploadVo, User user) throws Exception;




    /**
     * 修改了主版本数据
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(Long id,MultipartFile file,UploadVo uploadVo, User user) throws Exception;






    /**
     *     新增文件
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(MultipartFile file, UploadVo uploadVo, User user) throws Exception;


    String  findMasterFastUrlByFileId(String fileId) throws Exception;

    Long increaseFilePv(String fileId) throws Exception;

    void getAllFastPv() throws RedisConnectException;
}
