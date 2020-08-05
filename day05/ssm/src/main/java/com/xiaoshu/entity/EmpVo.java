package com.xiaoshu.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class EmpVo extends Emp{

	private String dname;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date start;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date end;
	public String getDname() {
		return dname;
	}

	public void setDname(String dname) {
		this.dname = dname;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
	
}
