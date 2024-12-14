package cn.xyt.codehub.mapper;

import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface StudentMapper extends BaseMapper<Student> {

}
