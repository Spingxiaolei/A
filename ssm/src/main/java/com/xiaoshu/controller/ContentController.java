package com.xiaoshu.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.dao.CategoryMapper;
import com.xiaoshu.entity.Category;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentVo;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.ContentService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("content")
public class ContentController extends LogController{
	static Logger logger = Logger.getLogger(ContentController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("contentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		List<Category> clist = contentService.findAllCategory();
		request.setAttribute("clist", clist);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "content";
	}
	
	
	@RequestMapping(value="contentList",method=RequestMethod.POST)
	public void userList(ContentVo contentVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
		
			String order = request.getParameter("order");
			String ordername = request.getParameter("ordername");
			
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<ContentVo> userList= contentService.findUserPage(contentVo,pageNum,pageSize,ordername,order);
			
		
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveContent")
	public void reserveUser(MultipartFile picFile,HttpServletRequest request,Content content,HttpServletResponse response) throws Exception{
		Integer contentid = content.getContentid();
		JSONObject result=new JSONObject();
		//判断图片是否有值
		if (picFile!=null && picFile.getSize()>0) {
			//获取图片名称
			String filename = picFile.getOriginalFilename();
			//获取图片后缀名
			String suffix = filename.substring(filename.lastIndexOf("."));
			//重新命名
			String newFileName =  System.currentTimeMillis()+suffix;
			//设置虚拟路径
			File file = new File("d:/img/"+newFileName);
			//上传图片
			picFile.transferTo(file);
			//将图片名称保存到数据库中
			content.setPinpath(newFileName);
		}
		
		try {
			if (contentid != null) {   // userId不为空 说明是修改
				Content userName = contentService.existUserWithUserName(content.getContenttitle());
				if(userName == null){
					content.setContentid(contentid);
					contentService.updateUser(content);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(contentService.existUserWithUserName(content.getContenttitle())==null){  // 没有重复可以添加
					contentService.addUser(content);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteContent")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String contentid : ids) {
				contentService.deleteUser(Integer.parseInt(contentid));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	//导入
	@RequestMapping("importContent")
	public void importContent(MultipartFile excelFile,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			//获取excel文件
			HSSFWorkbook wb = new HSSFWorkbook(excelFile.getInputStream());
			//获取文件中sheet页
			HSSFSheet sheet = wb.getSheetAt(0);
			//获取最后一行的行数
			int rowNum = sheet.getLastRowNum();
			//循环行数获取每一行对象
			for (int i = 1; i < rowNum; i++) {
				//获取每一行中单元格
				HSSFRow row = sheet.getRow(i);
				String contenttitle = row.getCell(0).getStringCellValue();
				String cname = row.getCell(1).getStringCellValue();
				String pinpath = row.getCell(2).getStringCellValue();
				String contenturl = row.getCell(3).getStringCellValue();
				String price = row.getCell(4).getStringCellValue();
				String status = row.getCell(5).getStringCellValue();
				Date createtime = row.getCell(6).getDateCellValue();
				//根据部门名称查询部门id
				Integer contencategoryid = findContenByCname(cname);
			
				//封装content对象
				Content content = new Content();
				content.setContenttitle(contenttitle);
				content.setContencategoryid(contencategoryid);
				content.setPinpath(pinpath);
				content.setContenturl(contenturl);
				content.setPrice(price);
				content.setStatus(status);
				content.setCreatetime(createtime);
				//调用service保存方法保存数据
				contentService.addUser(content);
			}
		
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入用户信息错误",e);
			result.put("errorMsg", "对不起，导入失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	//假设导入数据的时候部门导入的数据库中不存在怎么办
	private Integer findContenByCname(String cname) {
		Category category = new Category();
		category.setCategoryname(cname);
		Category one = categoryMapper.selectOne(category);
		if(one==null) {
			//添加部门，返回主键
			categoryMapper.insertCategory(category);
			one = category;
		}
		
		return one.getContentcategoryid();
	}


	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
