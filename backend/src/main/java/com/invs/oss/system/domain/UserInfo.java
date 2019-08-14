package com.invs.oss.system.domain;

import com.invs.oss.common.domain.RegexpConstant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class    UserInfo implements Serializable {

    private static final long serialVersionUID = -4852732617765810959L;

    private transient String id;

    @TableId(value = "USER_ID", type = IdType.AUTO)
    private Long userId;

  /*  @Size(min = 4, max = 10, message = "{range}")
    @NotBlank(message = "{required}")
    private String username;
*/

    @NotNull(message = "系统名称名称不能为空！")
    @ApiModelProperty(value="系统名称",name="sysName",required=true,example="系统名称")
    private String sysName;

    @NotNull(message = "系统名域名不能为空！")
    @ApiModelProperty(value="系统域名",name="sysDomain",required=true,example="系统域名")
    private String sysDomain;

    @NotNull(message = "系统负责人不能为空！")
    @ApiModelProperty(value="系统负责人",name="sysLeader",required=true,example="系统负责人")
    private String sysLeader;

    @Pattern(regexp = RegexpConstant.MOBILE_REG, message = "{mobile}")
    @NotNull(message = "手机号不能为空！")
    @ApiModelProperty(value="手机号",name="mobile",required=true,example="手机号")
    private String mobile;

    /**
     * shiro-redis v3.1.0 必须要有 getAuthCacheKey()或者 getId()方法
     * # Principal id field name. The field which you can get unique id to identify this principal.
     * # For example, if you use UserInfo as Principal class, the id field maybe userId, userName, email, etc.
     * # Remember to add getter to this id field. For example, getUserId(), getUserName(), getEmail(), etc.
     * # Default value is authCacheKey or id, that means your principal object has a method called "getAuthCacheKey()" or "getId()"
     *
     * @return userId as Principal id field name
     */
    public Long getAuthCacheKey() {
        return userId;
    }
}
