package cc.mrbird.febs.oss.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.oss.domain.UploadInfo;
import cc.mrbird.febs.oss.domain.UserSystemInfo;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank
 */
public interface UserSystemInfoService extends IService<UserSystemInfo> {
    UploadInfo uploadFile(MultipartFile files, UserSystemInfo userSystemInfo, HttpServletRequest request) throws Exception;

    IPage<UserSystemInfo> findSysInfoPage(User user, QueryRequest request);

    /**
    *  保存上传附件信息到数据库和redis
    *
    * @Author Frank
    * @Date Create in  2019/5/27 10:24
    * @param
    * @return
    */
    void saveUpload(UserSystemInfo userSystemInfo) throws Exception;
}
