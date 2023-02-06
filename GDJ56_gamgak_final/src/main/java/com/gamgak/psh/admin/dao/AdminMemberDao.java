package com.gamgak.psh.admin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;

import com.gamgak.psh.admin.vo.Member;
import com.gamgak.psh.admin.vo.Myres;

@Mapper
public interface AdminMemberDao{
	//정지회원
//	@Select("select cnt.* from member m right join(select member_no,count(*)stop from userreport group by member_no)cnt on m.member_no=cnt.member_no where cnt.stop=5")
	@Select("select cnt.* from member m right join(select member_no,count(*)stop from userreport group by member_no)cnt on m.member_no=cnt.member_no")
	List<Map> selectStopMember();
	
	//신고경고회원
//	@Select("select cnt.* from member m right join(select member_no,count(*)stop from userreport group by member_no)cnt on m.member_no=cnt.member_no where cnt.stop=3")
	@Select("select cnt.* from member m right join(select member_no,count(*)stop from userreport group by member_no)cnt on m.member_no=cnt.member_no")
	List<Map> selectReportMember();
	
	//삭제저장식당 게시글 회원
//	@Select("select cnt.myres,mr.member_no from myres mr right join(select myres_no,count(*)myres from myresreport group by myres_no)cnt on mr.myres_no=cnt.myres_no where cnt.myres=5")
	@Select("select cnt.myres,mr.member_no from myres mr right join(select myres_no,count(*)myres from myresreport group by myres_no)cnt on mr.myres_no=cnt.myres_no")
	List<Map> selectMyrestMember();
	
	//삭제모임 모임장
//	@Select("select cnt.mtcnt,mt.member_leader_no from meeting mt right join(select meeting_no,count(*)mtcnt from meetingreport group by meeting_no)cnt on mt.meeting_no=cnt.meeting_no where cnt.mtcnt=5")
	@Select("select cnt.mtcnt,mt.member_leader_no from meeting mt right join(select meeting_no,count(*)mtcnt from meetingreport group by meeting_no)cnt on mt.meeting_no=cnt.meeting_no")
	List<Map> selectMeetingMember();
	
	//리스트 페이지바
	@Select("select count(*) from ${table} where ${yn}='${ynval}'")
	int selectCount(Map param);
	
	//현재회원
	@Select("select count(*) from ${table} where ${yn}=${ynval}")
	int selectData(Map param);
	
	//신고 페이지바
	@Select("select count(*) from ${table} ur left join report r on ur.report_no=r.report_no where r.${yn}='${ynval}'")
	int selectReportCount(Map param);
	
	//저장 식당 리스트 페이지바
	@Select("select count(*) from ${table} where ${tableN}=${no} and ${yn}='${ynval}'")
	int selectCountNo(Map param);
	
	//상세페이지
	@Select("select * from member where member_no=${no}")
	Member selectMember(int no);
	
	//회원리스트
	@Select("select * from (select rownum as rnum, data.* from(select m.*,cnt.myres_cnt,rcnt.report_cnt from member m join(select a.member_no,count(myres_no) myres_cnt from member a left join myres b on a.member_no=b.member_no group by a.member_no)cnt on m.member_no=cnt.member_no left join (select member_no,count(report_no) report_cnt from userreport group by member_no) rcnt on rcnt.member_no=m.member_no order by m.member_no)data where ${yn}='${ynval}') where rnum between (${cPage}-1)*${numPerpage}+1 and ${cPage}*${numPerpage}")
	List<Map> selectMemberData(Map param);
	
	//저장식당리스트
	@Select("select * from(select rownum as rnum,data.* from (select * from myres my left join (select myres_no,count(mr.report_no) report_cnt from myresreport mr left join report r on mr.report_no=r.report_no where r.solve_yn='N' group by myres_no)report_cnt on report_cnt.myres_no=my.myres_no where member_no=${no} and del_yn='N')data)where rnum between (${cPage}-1)*${numPerpage}+1 and ${cPage}*${numPerpage}")
	@Results({
		@Result(property="MYRES_MEMO", column="MYRES_MEMO",jdbcType = JdbcType.CLOB, javaType = String.class)
	})
	List<Map> selectMyresList(Map param);
	
	//모임리스트
	@Select("select * from(select rownum as rnum, data.* from(select * from meeting mt left join (select count(report_no)mtr_cnt,meeting_no from meetingreport group by meeting_no)mt_cnt on mt.meeting_no=mt_cnt.meeting_no)data where ${yn}='${ynval}') where rnum between (${cPage}-1)*${numPerpage}+1 and ${cPage}*${numPerpage}")
	@Results({
		@Result(property="MEETING_DATE", column="MEETING_DATE",jdbcType = JdbcType.TIMESTAMP, javaType = String.class)
		})
	List<Map> selectMeetingList(Map param);
	
	//신고리스트
	@Select("select * from(select rownum as rnum,data.*from(select * from ${tableN} t left join report r on r.report_no=t.report_no where r.${yn}='${ynval}' order by r.report_no )data ) where rnum between (${cPage}-1)*${numPerpage}+1 and ${cPage}*${numPerpage}")
	@Results({
			@Result(property="REPORT_REASON", column="REPORT_REASON",jdbcType = JdbcType.CLOB, javaType = String.class)
	})
	List<Map> selectReportList(Map param);
	
	//관리자 삭제
	@Update("update ${tableN} set ${yn}='Y' where ${columnN}=${no}")
	int deleteData(int no,String tableN,String columnN,String yn);
}
