package com.invs.oss.oss.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: nanJunYu
 * @Description:上传完成返回对象
 * @Date: Create in  2018/8/2 11:12
 */
@Data
public class UploadInfo {

    private LocalDateTime createTime;

    private String fileName;

    private Long fileSize;

    private String url;

    private int code;

}
