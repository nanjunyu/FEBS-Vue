package cc.mrbird.febs.oss.dao;


import cc.mrbird.febs.oss.domain.FileCurrent;
import cc.mrbird.febs.oss.domain.FileHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Frank
 */
public interface FileCurrentMapper extends BaseMapper<FileCurrent> {


    List<FileCurrent> findFileCurrentPage(Map<String,Object> map);
    int findFileCurrentCount(Map<String,Object> map);


}
