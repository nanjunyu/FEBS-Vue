package com.invs.oss.system.dao;

import com.invs.oss.system.domain.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface DeptMapper extends BaseMapper<Dept> {

	/**
	 * 递归删除部门
	 *
	 * @param deptId deptId
	 */
	void deleteDepts(String deptId);
}