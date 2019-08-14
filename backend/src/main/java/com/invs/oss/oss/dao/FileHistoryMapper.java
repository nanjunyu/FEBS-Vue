package com.invs.oss.oss.dao;


import com.invs.oss.oss.domain.FileHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Frank
 */
public interface FileHistoryMapper extends BaseMapper<FileHistory> {
    FileHistory getMaxVersion(Long parentId);

    Integer sumPvByParentId(Long parentId);


    Map<String,Object>  sysFileInfo(Long userId);

    List<FileHistory> filePvInfo(Map<String,Object> map);
}
