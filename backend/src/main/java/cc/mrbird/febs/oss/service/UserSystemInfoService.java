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
    UploadInfo uploadFile(MultipartFile files, UserSystemInfo userSystemInfo, HttpServletRequest request) throws FebsException;

    IPage<UserSystemInfo> findSysInfoPage(User user, QueryRequest request);
}
