package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.dao.EmpMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpExample;
import com.xiaoshu.entity.EmpExample.Criteria;
import com.xiaoshu.entity.EmpVo;

@Service
public class EmpService {

	

	@Autowired
	private EmpMapper empMapper;
	// 新增
	public void addEmp(Emp t) throws Exception {
		empMapper.insert(t);
	};
	// 修改
	public void updateEmp(Emp t) throws Exception {
		empMapper.updateByPrimaryKeySelective(t);
	};
	// 删除
	public void deleteEmp(Integer id) throws Exception {
		empMapper.deleteByPrimaryKey(id);
	};


	// 通过用户名判断是否存在，（新增时不能重名）
	public Emp existUserWithUserName(String userName) throws Exception {
		EmpExample example = new EmpExample();
		Criteria criteria = example.createCriteria();
		criteria.andENameEqualTo(userName);
		List<Emp> userList = empMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};
	

	public PageInfo<EmpVo> findEmpPage(EmpVo emp, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		//查询列表集合
		List<EmpVo> list = empMapper.findPage(emp);
		PageInfo<EmpVo> pageInfo = new PageInfo<EmpVo>(list);
		return pageInfo;
	}
	@Autowired
	private DeptMapper deptMapper;
	public List<Dept> findDeptList() {
		return deptMapper.selectAll();
	}


}
