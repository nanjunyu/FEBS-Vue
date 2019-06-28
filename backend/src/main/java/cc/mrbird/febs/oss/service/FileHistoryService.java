package cc.mrbird.febs.oss.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.oss.domain.FileHistory;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UploadVo;
import cc.mrbird.febs.system.domain.User;
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
    void setPresent(Long id, User user);

    /**
     * 保存上传附件信息到数据库和redis
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/5/27 10:24
     */
    void saveUpload(FileHistory fileHistory) throws Exception;

    IPage<?> findSysInfoPageByParentId(String parentId, String userId, QueryRequest queryRequest);




    /**
     *    修改了子版本数据
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return cc.mrbird.febs.oss.domain.UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(Long id,Long ParentId,MultipartFile file,UploadVo uploadVo, User user) throws Exception;




    /**
     * 修改了主版本数据
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return cc.mrbird.febs.oss.domain.UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(Long id,MultipartFile file,UploadVo uploadVo, User user) throws Exception;






    /**
     *     新增文件
     * @param uploadVo 附件对象
     * @param user     当前登录用户
     * @return cc.mrbird.febs.oss.domain.UploadInfo  上传成功对象
     * @Author Frank
     *
     * @Date Create in  2019/6/19 14:35
     */
    UploadInfo uploadFile(MultipartFile file, UploadVo uploadVo, User user) throws Exception;
}
