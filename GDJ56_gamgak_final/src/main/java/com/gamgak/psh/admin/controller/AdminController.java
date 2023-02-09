package com.gamgak.psh.admin.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gamgak.psh.admin.service.AdminService;
import com.gamgak.psh.admin.vo.Member;
import com.gamgak.psh.admin.vo.Myres;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.gamgak.psh.admin.common.PageFactory;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

	private AdminService service;
	@Autowired
	public AdminController(AdminService service) {
		super();
		this.service = service;
	}
	
	@RequestMapping("/")
	public String adminMainPage(Model m) {
		m.addAttribute("memdata", service.selectData(Map.of("table","MEMBER","yn","MEMBER_ENROLLDATE","ynval","SYSDATE")));
		m.addAttribute("redata", service.selectData(Map.of("table","REPORT","yn","REPORT_DATE","ynval","SYSDATE")));
		m.addAttribute("mtdata", service.selectData(Map.of("table","MEETING","yn","MEETING_ENROLL_DATE","ynval","SYSDATE")));
		return "psh_admin/main";
	}
	
	@RequestMapping("/member.do")
	public String adminMemberPage(Model m) {
		//회원 수
		int total=service.selectCount(Map.of("table","member","yn","authority_yn","ynval","N"));
		m.addAttribute("total",total);
		//탈퇴회원 수
		int authmem=service.selectCount(Map.of("table","member","yn","authority_yn","ynval","Y"));
		m.addAttribute("authmem",authmem);
		
		m.addAttribute("memdata", service.selectData(Map.of("table","MEMBER","yn","MEMBER_ENROLLDATE","ynval","SYSDATE")));
		m.addAttribute("redata", service.selectData(Map.of("table","REPORT","yn","REPORT_DATE","ynval","SYSDATE")));
		m.addAttribute("mtdata", service.selectData(Map.of("table","MEETING","yn","MEETING_ENROLL_DATE","ynval","SYSDATE")));
		
		return "/psh_admin/memberlist";
	}
	
	@RequestMapping("/selectmember.do")
	@ResponseBody
	public Map<String,Object> selectMemberAll(@RequestParam(value="cPage",defaultValue="1")int cPage,
			String ynval,String functionN,String yn) {
		Map<String,Object> memberlist=new HashMap<String, Object>();
//		@RequestParam(value="numPerpage",defaultValue="5") int numPerpage
//		System.out.println(ynval);
		int numPerpage=5;
		String table="MEMBER";
		
		//회원 수
		int total=service.selectCount(Map.of("table",table,"yn",yn,"ynval","N"));
		memberlist.put("totalmem", total);
		//탈퇴회원 수
		memberlist.put("authmem",service.selectCount(Map.of("table",table,"yn",yn,"ynval","Y")));
		
		List<Map> list=service.selectMemberData(Map.of("cPage",cPage,"numPerpage",numPerpage,"yn",yn,"ynval",ynval));
		System.out.println(total);
		memberlist.put("list",list);
		memberlist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"member.do",functionN));
		
		return memberlist;
	}
	@RequestMapping("/myresview.do")
	public String myresView( Model m,int no) {
		m.addAttribute("no",no);
		
		m.addAttribute("memdata", service.selectData(Map.of("table","MEMBER","yn","MEMBER_ENROLLDATE","ynval","SYSDATE")));
		m.addAttribute("redata", service.selectData(Map.of("table","REPORT","yn","REPORT_DATE","ynval","SYSDATE")));
		m.addAttribute("mtdata", service.selectData(Map.of("table","MEETING","yn","MEETING_ENROLL_DATE","ynval","SYSDATE")));
		return "/psh_admin/myresview";
	}
	@RequestMapping("/selectmyres.do")
	@ResponseBody
	public Map<String,Object> selectMyresAll(@RequestParam(value="cPage",defaultValue="1")int cPage,
			String functionN,int no,String tableN,String yn,String ynval) {
		Map<String,Object> myreslist=new HashMap<String, Object>();
		int numPerpage=5;
		String table="MYRES";
		
		int total=service.selectCountNo(Map.of("table",table,"tableN",tableN,"no",no,"yn",yn,"ynval",ynval));
//		System.out.println(total);
		myreslist.put("member",service.selectMember(no));
		myreslist.put("list",service.selectMyresList(Map.of("cPage",cPage,"numPerpage",numPerpage,"no",no,"tableN",tableN,"table",table,"yn",yn)));
		myreslist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"myresview.do",functionN));
		return myreslist;
	}
	
	@RequestMapping("/meeting.do")
	public String adminMeetingPage(Model m) {
		int total=service.selectCount(Map.of("table","MEETING","yn","del_yn","ynval","N"));
		int delmt=service.selectCount(Map.of("table","MEETING","yn","del_yn","ynval","Y"));
		
		m.addAttribute("totalmeeting",total);
		m.addAttribute("delmt",delmt);
		
		m.addAttribute("memdata", service.selectData(Map.of("table","MEMBER","yn","MEMBER_ENROLLDATE","ynval","SYSDATE")));
		m.addAttribute("redata", service.selectData(Map.of("table","REPORT","yn","REPORT_DATE","ynval","SYSDATE")));
		m.addAttribute("mtdata", service.selectData(Map.of("table","MEETING","yn","MEETING_ENROLL_DATE","ynval","SYSDATE")));
		return "/psh_admin/meetinglist";
	}
	
	@RequestMapping("/selectmeeting.do")
	@ResponseBody
	public Map<String,Object> selectMeetingAll(@RequestParam(value="cPage",defaultValue="1")int cPage,
			String functionN,String tableN,String yn,String ynval) {
		Map<String,Object> meetinglist=new HashMap<String, Object>();
//		@RequestParam(value="numPerpage",defaultValue="5") int numPerpage
		
		int numPerpage=5;
		String table="MEETING";

		int total=service.selectCount(Map.of("table",table,"yn",yn,"ynval","N"));
		List<Map> list=service.selectMeetingData(Map.of("cPage",cPage,"numPerpage",numPerpage,"yn",yn,"ynval",ynval));
		System.out.println(total);
		meetinglist.put("list",list);
		meetinglist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"meeting.do",functionN));
		
		
		return meetinglist;
	}
	
	@RequestMapping("/report.do")
	public String adminReportPage(Model m) {
		
		//오늘 접수된 신고
		int today=service.selectData(Map.of("table","report","yn","report_date","ynval","sysdate"));
		//총 신고접수
		int total=service.selectCount(Map.of("table","report","yn","solve_yn","ynval","N"));
				
		//유저신고 처리 안한거
				
		//맛집저장 신고 처리 안한 수 
				
		//모임신고 처리 안한 수 
		m.addAttribute("today",today);
		m.addAttribute("total",total);
		
		m.addAttribute("memdata", service.selectData(Map.of("table","MEMBER","yn","MEMBER_ENROLLDATE","ynval","SYSDATE")));
		m.addAttribute("redata", service.selectData(Map.of("table","REPORT","yn","REPORT_DATE","ynval","SYSDATE")));
		m.addAttribute("mtdata", service.selectData(Map.of("table","MEETING","yn","MEETING_ENROLL_DATE","ynval","SYSDATE")));
		return "psh_admin/reportlist";
	}
	
	@RequestMapping("/userreport.do")
	@ResponseBody
	public Map<String,Object> selectUserReport(@RequestParam String tableN,@RequestParam(value="cPage",defaultValue="1")int cPage,
			String functionN,String yn,String ynval) {
		Map<String,Object> reportlist=new HashMap<String, Object>();
		int numPerpage=5;
		
		//정지알림(신고횟수가 5)
		reportlist.put("stopmem",service.selectStopMember());
		//신고알림(신고횟수가 3)
		reportlist.put("reportmem",service.selectReportMember());
		//게시글 삭제 알림
		reportlist.put("myresmem",service.selectMyrestMember());		
		//모임 삭제 알림
		reportlist.put("meetingmem",service.selectMeetingMember());
		
		System.out.println(reportlist);
		int total=service.selectReportCount(Map.of("table",tableN,"yn",yn,"ynval",ynval));
		reportlist.put("list",service.selectReportData(Map.of("cPage",cPage,"numPerpage",numPerpage,"tableN",tableN,"yn",yn,"ynval",ynval)));
		reportlist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"meeting.do",functionN));
		
//		System.out.println(reportlist);
		return reportlist;
	}
	
	@RequestMapping("/myresreport.do")
	@ResponseBody
	public Map<String,Object> selectMyresReport(@RequestParam String tableN,@RequestParam(value="cPage",defaultValue="1")int cPage,
			String functionN,String yn,String ynval) {
		Map<String,Object> reportlist=new HashMap<String, Object>();
		int numPerpage=5;
		int total=service.selectReportCount(Map.of("table",tableN,"yn",yn,"ynval",ynval));
		reportlist.put("list",service.selectReportData(Map.of("cPage",cPage,"numPerpage",numPerpage,"tableN",tableN,"yn",yn,"ynval",ynval)));
		reportlist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"meeting.do",functionN));
		
		return reportlist;
	}
	
	@RequestMapping("/meetingreport.do")
	@ResponseBody
	public Map<String,Object> selectMeetingReport(@RequestParam String tableN,@RequestParam(value="cPage",defaultValue="1")int cPage,
			String functionN,String yn,String ynval) {
		Map<String,Object> reportlist=new HashMap<String, Object>();
		int numPerpage=5;
		int total=service.selectReportCount(Map.of("table",tableN,"yn",yn,"ynval",ynval));
		reportlist.put("list",service.selectReportData(Map.of("cPage",cPage,"numPerpage",numPerpage,"tableN",tableN,"yn",yn,"ynval",ynval)));
		reportlist.put("pageBar",PageFactory.getPage(cPage, numPerpage, total,"meeting.do",functionN));
		
		return reportlist;
	}
	
	@RequestMapping("checkdata.do")
	@ResponseBody
	public int checkdata(String stopmem,String reportmem,String myresmem,String meetingmem) {
		System.out.println("stopmem : "+stopmem);
		String data=stopmem.substring(stopmem.length()-(stopmem.length()-1),stopmem.length()-1);
		String list[]=data.split(",");
		for(int i=0;i<list.length;i++) {
			System.out.println("stopmem 파싱 "+list[i]);
		}
		System.out.println("reportmem : "+reportmem);
		System.out.println("myresmem : "+myresmem);
		System.out.println("meetingmem : "+meetingmem);
		return 1;
	}
	@RequestMapping("/deletedata.do")
	@ResponseBody
	public Map<String,Object> deleteData(@RequestParam Map nodata, String tableN,String columnN,String yn) {
		String data=(String)(nodata.get("nodata"));
//		System.out.println(nodata);
		Map result=new HashMap();
		List total=new ArrayList();
		String datas=data.substring(data.length()-(data.length()-1),data.length()-1);
		String list[]=datas.split(",");
		for(int i=0;i<list.length;i++) {
			int result1=service.deleteData(Integer.parseInt(list[i].substring(list[i].length()-(list[i].length()-1),list[i].length()-1)),tableN,columnN,yn);
			total.add(result1);
		}
		result.put("result", total);
//		System.out.println(result.get(result));
		return result;
	}
	
}
