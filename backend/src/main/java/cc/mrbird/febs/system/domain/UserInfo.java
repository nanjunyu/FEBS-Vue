package cc.mrbird.febs.system.domain;

import cc.mrbird.febs.common.domain.RegexpConstant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -4852732617765810959L;

    private transient String id;

    @TableId(value = "USER_ID", type = IdType.AUTO)
    private Long userId;

  /*  @Size(min = 4, max = 10, message = "{range}")
    @NotBlank(message = "{required}")
    private String username;
*/

    @NotBlank(message = "{required}")
    private String sysName;

    @NotBlank(message = "{required}")
    private String sysDomain;

    @NotBlank(message = "{required}")
    private String sysLeader;

    @Pattern(regexp = RegexpConstant.MOBILE_REG, message = "{mobile}")
    @NotBlank(message = "{required}")
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
