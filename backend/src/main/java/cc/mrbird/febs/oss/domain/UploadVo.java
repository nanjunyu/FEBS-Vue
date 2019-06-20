package cc.mrbird.febs.oss.domain;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
*
*  上传文件对象
* @Author Frank
* @Date Create in  2019/6/11 9:44
* @param
* @return
*/
@Data
public class UploadVo {

	@NotNull(message = "文件名称不能为空！")
	@ApiModelProperty(value="文件名称",name="fileName",required=true,example="风险揭示书")
	private String fileName;

	@NotNull(message = "文件状态不能为空！")
	@ApiModelProperty(value="文件状态--0在用版本 1已废弃",name="status",required=true,example="0")
	private String status;

	@NotNull(message = "版本号不能为空！")
	@ApiModelProperty(value="版本号",name="versionNumber",required=true,example="1.0")
	private String versionNumber;


	@ApiModelProperty(value="id",name="id",required=false,example="1")
	private String id;


	@ApiModelProperty(value="父级id",name="parentId",required=false,example="1")
	private String parentId;


}
