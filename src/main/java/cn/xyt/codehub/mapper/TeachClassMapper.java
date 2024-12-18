package cn.xyt.codehub.mapper;

import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface TeachClassMapper extends BaseMapper<TeachClass> {
    @Select("SELECT s.* FROM student s " +
            "JOIN student_class sc ON s.student_number = sc.student_number " +
            "WHERE sc.class_id = #{classId}")
    List<Student> getStudentsByClassId(@Param("classId") Long classId);
}
