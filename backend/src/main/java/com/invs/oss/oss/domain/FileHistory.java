package com.invs.oss.oss.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统文件表
 *
 * @author Frank
 */
@TableName("t_file_history")
@Data
public class FileHistory  extends FileCurrent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long parentId;


}
