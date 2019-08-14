package cc.mrbird.febs.oss.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.oss.domain.FileCurrent;
import cc.mrbird.febs.system.domain.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Frank
 */
public interface FileCurrentService extends IService<FileCurrent> {
    /**
     * 获取主文件列表
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/18 14:19
     */
    IPage<FileCurrent> findFileCurrentPage(User user, QueryRequest request);

    /**
     * 获取主文件列表管理员
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/18 14:19
     */
    IPage<FileCurrent> findFileCurrentPage(QueryRequest request);
}
