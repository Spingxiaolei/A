package com.xiaoshu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpExample;
import com.xiaoshu.entity.EmpVo;

public interface EmpMapper extends BaseMapper<Emp> {
    long countByExample(EmpExample example);

    int deleteByExample(EmpExample example);

    List<Emp> selectByExample(EmpExample example);

    int updateByExampleSelective(@Param("record") Emp record, @Param("example") EmpExample example);

    int updateByExample(@Param("record") Emp record, @Param("example") EmpExample example);

	List<EmpVo> findPage(EmpVo emp);
}