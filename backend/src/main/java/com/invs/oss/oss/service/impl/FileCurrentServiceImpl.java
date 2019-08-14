package com.invs.oss.oss.service.impl;

import com.invs.oss.oss.common.OssConstant;
import com.invs.oss.oss.dao.FileCurrentMapper;
import com.invs.oss.oss.domain.FileCurrent;
import com.invs.oss.oss.service.FileCurrentService;
import com.invs.oss.system.domain.User;
import com.invs.oss.common.domain.QueryRequest;
import com.invs.oss.common.fastdfs.FastDfsConstant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Frank
 */
@Slf4j
@Service("fileCurrentService")
public class FileCurrentServiceImpl extends ServiceImpl<FileCurrentMapper, FileCurrent> implements FileCurrentService {

    @Autowired
    FileCurrentMapper fileCurrentMapper;

    @Autowired
    FastDfsConstant fastDfsConstant;

    @Override
    public IPage<FileCurrent> findFileCurrentPage(User user, QueryRequest request) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            int pageSize = request.getPageSize();
            int pageNum = request.getPageNum();
            int pageStart = (pageNum - 1) * pageSize;
            if (pageStart < 0) {
                pageStart = 0;
            }
            map.put("pageStart", pageStart);
            map.put("pageSize", pageSize);
            map.put("userId", user.getUserId());
            map.put("status", OssConstant.FILE_STATUS_CURRENT);
            List<FileCurrent> fileCurrentList = fileCurrentMapper.findFileCurrentPage(map);
            Page<FileCurrent> page = new Page<>();
            page.setRecords(fileCurrentList);
            page.setTotal(fileCurrentMapper.findFileCurrentCount(map));
            return page;
        } catch (Exception e) {
            log.error("获取附件列表失败", e);
            return null;
        }
    }

    @Override
    public IPage<FileCurrent> findFileCurrentPage(QueryRequest request) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            int pageSize = request.getPageSize();
            int pageNum = request.getPageNum();
            int pageStart = (pageNum - 1) * pageSize;
            if (pageStart < 0) {
                pageStart = 0;
            }
            map.put("pageStart", pageStart);
            map.put("pageSize", pageSize);
            map.put("status", OssConstant.FILE_STATUS_CURRENT);
            List<FileCurrent> fileCurrentList = fileCurrentMapper.findFileCurrentPage(map);
            Page<FileCurrent> page = new Page<>();
            page.setRecords(fileCurrentList);
            page.setTotal(fileCurrentMapper.findFileCurrentCount(map));
            return page;
        } catch (Exception e) {
            log.error("获取附件列表失败", e);
            return null;
        }
    }
}
