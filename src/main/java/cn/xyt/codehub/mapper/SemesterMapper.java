package cn.xyt.codehub.mapper;

import cn.xyt.codehub.entity.Semester;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface SemesterMapper extends BaseMapper<Semester> {

    /**
     * 重置所有学期的当前状态
     */
    @Update("UPDATE semester SET is_current = 0 WHERE is_current = 1;")
    void resetCurrentSemester();

    /**
     * 设置指定学期为当前学期
     * @param semesterId 学期ID
     */
    @Update("UPDATE semester SET is_current = 1 WHERE id = #{semesterId}")
    void updateCurrentSemester(Long semesterId);
}
