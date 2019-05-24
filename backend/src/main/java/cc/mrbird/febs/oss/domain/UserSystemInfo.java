package cc.mrbird.febs.oss.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统文件表
 *
 * @author Frank
 */
@Data
@TableName("user_system_info")
public class UserSystemInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 文件名称
     */
    @NotBlank(message = "{required}")
    private String fileName;

    /**
     * mime
     */
    private String mimeType;

    /**
     * 附件的后缀
     */
    private String suffix;

    /**
     * 文件大小 字节单位
     */
    private BigDecimal fileSize;

    /**
     * 文件状态 0在用版本 1已废弃
     */
    @NotBlank(message = "{required}")
    private String status;

    /**
     * fastDfs上传完成后原始地址
     */
    private String fastPath;

    /**
     * 上传路径，用户选择的上传地址
     */
    private String uploadPath;

    /**
     * 上传完成之后生成的文件id 字符串
     */
    private String fileId;

    /**
     * 版本号
     */
    private BigDecimal versionNumber;

    /**
     * 上传人
     */
    private String author;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 文件的md5值
     */
    private String md5;


    /*@NotBlank(message = "{required}")
    private MultipartFile file;*/
}
