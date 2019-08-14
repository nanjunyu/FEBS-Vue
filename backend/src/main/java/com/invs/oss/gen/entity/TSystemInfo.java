package com.invs.oss.gen.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户系统表
 *
 * @author Frank
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TSystemInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 系统域名
     */
    private String systemDomain;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 系统负责人
     */
    private String systemLeader;

    /**
     * bucket名称
     */
    private String systemName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


}
