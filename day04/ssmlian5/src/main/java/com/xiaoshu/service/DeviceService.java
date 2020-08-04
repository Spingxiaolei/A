package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.DeviceMapper;
import com.xiaoshu.dao.DevicetypeMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.DeviceVo;
import com.xiaoshu.entity.Devicetype;

@Service
public class DeviceService {

	@Autowired
	UserMapper userMapper;

	@Autowired
	DeviceMapper deviceMapper;
	
	@Autowired
	DevicetypeMapper devicetypeMapper;

	// 新增
	public void addUser(Device d) throws Exception {
		deviceMapper.insert(d);
	};

	// 修改
	public void updateUser(Device d) throws Exception {
		deviceMapper.updateByPrimaryKeySelective(d);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		deviceMapper.deleteByPrimaryKey(id);
	};

	//展示
	public PageInfo<DeviceVo> findUserPage(DeviceVo deviceVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<DeviceVo> list = deviceMapper.findDevice(deviceVo);
		return new PageInfo<>(list);
	}
	
	//查询部门
	public List<Devicetype> findType(){
		return devicetypeMapper.selectAll();
	}

	public List<DeviceVo> findDevice(DeviceVo deviceVo) {
		// TODO Auto-generated method stub
		return deviceMapper.findDevice(deviceVo);
	}

	public List<DeviceVo> findDeviceList(DeviceVo deviceVo) {
		List<DeviceVo> list = deviceMapper.findDevice(deviceVo);
		return list;
	}


}
