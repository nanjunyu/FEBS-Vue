package cc.mrbird.febs.oss.dao;


import cc.mrbird.febs.oss.domain.UserSystemInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Frank
 */
public interface UserSystemInfoMapper extends BaseMapper<UserSystemInfo> {

    IPage<UserSystemInfo> findSysFileInfoPage(Page page, @Param("userId") Long userId);
}
