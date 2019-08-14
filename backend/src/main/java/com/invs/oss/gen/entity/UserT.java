package com.invs.oss.gen.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 
 *
 * @author Frank
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserT implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机
     */
    private String telephone;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 性别 0 不区分 1 男 2 女
     */
    private Integer sex;

    /**
     * 注册时间
     */
    private LocalDateTime regTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态 0 未激活 1 已激活 2 禁用
     */
    private Integer status;

    /**
     * openId
     */
    private String openId;


}
