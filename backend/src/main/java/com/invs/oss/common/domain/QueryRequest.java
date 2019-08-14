package com.invs.oss.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = -4869594085374385813L;

    @ApiModelProperty(value="分页每页条数",name="pageSize",required=true,example="10")
    private int pageSize = 10;

    @ApiModelProperty(value="页数",name="pageNum",required=true,example="1")
    private int pageNum = 1;

    private String sortField;
    private String sortOrder;
}
