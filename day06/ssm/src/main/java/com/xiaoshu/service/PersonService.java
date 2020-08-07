package com.xiaoshu.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonExample;
import com.xiaoshu.entity.PersonExample.Criteria;
import com.xiaoshu.entity.User;


@Service
public class PersonService {


	@Autowired
	private PersonMapper personMapper;

	public void addPerson(Person t) throws Exception {
		personMapper.insert(t);
	};
	// 修改
	public void updatePerson(Person t) throws Exception {
		personMapper.updateByPrimaryKeySelective(t);
	};
	// 删除
	public void deletePerson(Integer id) throws Exception {
		personMapper.deleteByPrimaryKey(id);
	};

	// 通过用户名判断是否存在，（新增时不能重名）
	public Person existUserWithUserName(String personName) throws Exception {
		PersonExample example = new PersonExample();
		Criteria criteria = example.createCriteria();
		criteria.andPnameEqualTo(personName);
		List<Person> userList = personMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	
	public PageInfo<Person> findUserPage(Person person, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		//创建example对象，用于封装参数
		PersonExample example = new PersonExample();
		Criteria criteria = example.createCriteria();
		//根据用户名模糊查询
		if(person.getPname()!=null && person.getPname()!=""){
			criteria.andPnameLike("%"+person.getPname()+"%");
		}
		if(person.getGender()!=null && person.getGender()!=""){
			criteria.andGenderEqualTo(person.getGender());
		}
		List<Person> list = personMapper.selectByExample(example);
		PageInfo<Person> pageInfo = new PageInfo<Person>(list);
		return pageInfo;
	}


}
