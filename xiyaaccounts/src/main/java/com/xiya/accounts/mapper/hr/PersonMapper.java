package com.xiya.accounts.mapper.hr;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiya.accounts.pojo.Person;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonMapper extends BaseMapper<Person> {
    @Select("select p.guid,p.jobnum,p.sname,o.lname organcode,ps.pname post,account_lock,status from hr.person p " +
            "join comm.organ o on o.id = p.organcode " +
            "join comm.post ps on ps.id = p.post " +
            "where p.jobnum = #{jobnum} " +
            "and p.isemp = '1' "
    )
    Person getPerson(String jobnum);
}
