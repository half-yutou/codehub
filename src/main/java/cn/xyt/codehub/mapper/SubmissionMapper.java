package cn.xyt.codehub.mapper;

import cn.xyt.codehub.entity.Submission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubmissionMapper extends BaseMapper<Submission> {

    // 根据 classId 查询提交记录
    @Select("SELECT * FROM submission WHERE class_id = #{classId}")
    List<Submission> getSubmissionsByClassId(@Param("classId") Long classId);

    // 根据 studentId 查询提交记录
    @Select("SELECT * FROM submission WHERE student_id = #{studentId}")
    List<Submission> getSubmissionsByStudentId(@Param("studentId") Long studentId);

    // 根据 classId 和 studentId 查询提交记录
    @Select("SELECT * FROM submission WHERE class_id = #{classId} AND student_id = #{studentId}")
    List<Submission> getSubmissionsByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
}

