package com.invs.oss.oss.controller;

import com.invs.oss.oss.dao.FileHistoryMapper;
import com.invs.oss.system.domain.User;
import com.invs.oss.common.utils.DateUtils;
import com.invs.oss.common.utils.FebsUtil;
import com.invs.oss.oss.domain.FileHistory;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计相关
 *
 * @Author Frank
 * @Date Create in  2019/7/8 10:16
 */
@Slf4j
@Validated
@RestController
@Api(tags = "统计相关api")
@RequestMapping("/dashBoard")
public class DashBoardController {

    @Autowired
    FileHistoryMapper fileHistoryMapper;

    /**
     * 访问统计
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/10 14:06
     */
    @ApiOperation(value = "根据时间段等筛选查询文件访问情况", notes = "根据时间段筛选查询文件访问情况")
    @GetMapping("getFilePvInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "fileName", value = "文件名称", paramType = "query", dataType = "String")
    })
    public List<FileHistory> getFilePvInfo(@ApiParam(name = "startTime", value = "开始时间") String startTime, @ApiParam(name = "endTime", value = "结束时间") String endTime, @ApiParam(name = "fileName", value = "文件名称") String fileName) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("startTime", DateUtils.getStartTimeofDay(DateUtils.getNextDay(new Date(), 30)));
        map.put("endTime", DateUtils.getEndTimeofDay(new Date()));
        User user = FebsUtil.getCurrentUser();
        map.put("userId", user.getUserId());
        if (!StringUtils.isEmpty(startTime)) {
            map.put("startTime", startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            map.put("endTime", endTime);
        }
        if (!StringUtils.isEmpty(fileName)) {
            map.put("fileName", fileName);
        }
        List<FileHistory> fileHistoryList = fileHistoryMapper.filePvInfo(map);
        return fileHistoryList;
    }

}