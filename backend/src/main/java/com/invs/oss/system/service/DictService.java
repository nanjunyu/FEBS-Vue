package com.invs.oss.system.service;

import com.invs.oss.common.domain.QueryRequest;
import com.invs.oss.system.domain.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


public interface DictService extends IService<Dict> {

    IPage<Dict> findDicts(QueryRequest request, Dict dict);

    void createDict(Dict dict);

    void updateDict(Dict dicdt);

    void deleteDicts(String[] dictIds);

}
