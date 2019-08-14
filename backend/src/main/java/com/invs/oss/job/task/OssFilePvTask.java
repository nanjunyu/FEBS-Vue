package com.invs.oss.job.task;

import com.invs.oss.common.exception.RedisConnectException;
import com.invs.oss.oss.service.FileHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/**
 *   附件定时更新浏览次数
 *
 * @Author Frank
 * @Date Create in  2019/6/28 14:26
 * @param
 * @return
 */
public class OssFilePvTask {


    @Autowired
    FileHistoryService fileHistoryService;

    /**
     * 将redis中缓存的 浏览次数更新到数据库中
     *
     * @param
     * @return
     * @Author Frank
     * @Date Create in  2019/6/28 14:41
     */
    public void syncRedisToDb() {
        try {
            fileHistoryService.getAllFastPv();
        } catch (RedisConnectException e) {
            e.printStackTrace();
        }
    }


}
