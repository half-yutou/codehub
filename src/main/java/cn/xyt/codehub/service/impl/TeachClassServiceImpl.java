package cn.xyt.codehub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.StudentExcelDTO;
import cn.xyt.codehub.dto.TeachClassDTO;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.StudentClass;
import cn.xyt.codehub.mapper.SemesterMapper;
import cn.xyt.codehub.mapper.StudentClassMapper;
import cn.xyt.codehub.mapper.StudentMapper;
import cn.xyt.codehub.service.StudentClassService;
import cn.xyt.codehub.service.StudentService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.util.ExcelUtil;
import cn.xyt.codehub.vo.StudentVO;
import cn.xyt.codehub.vo.TeachClassVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.xyt.codehub.entity.TeachClass;
import cn.xyt.codehub.mapper.TeachClassMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeachClassServiceImpl extends ServiceImpl<TeachClassMapper, TeachClass> implements TeachClassService {
    @Resource
    private TeachClassMapper teachClassMapper;

    @Resource
    private SemesterMapper semesterMapper;

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private StudentService studentService;

    @Resource
    private StudentClassMapper studentClassMapper;
    @Resource
    private StudentClassService studentClassService;

    // region student-manage

    @Override
    public void flushStudentCount(Long classId) {
        Long count = studentClassMapper.selectCount(new QueryWrapper<StudentClass>()
                .eq("class_id", classId));
        update().eq("id", classId).set("count", count).update();
    }

    @Override
    public List<StudentVO> getStudentsByClassId(Long classId) {
        List<Student> students = teachClassMapper.getStudentsByClassId(classId);
        return students.stream()
                .map(student -> BeanUtil.copyProperties(student, StudentVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean addSingleStudentToClass(Long classId, String studentNumber) {
        // 先检查学生表内有无该学生
        Student studentInDb = studentMapper.selectOne(new QueryWrapper<Student>()
                .eq("student_number", studentNumber));
        if (studentInDb == null) {
            throw new RuntimeException("学生不存在");
        }

        // 将该学生和班级信息添加到中间表
        int i = studentClassMapper.insert(new StudentClass(studentNumber, classId));
        if (i == 0) {
            throw new RuntimeException("添加失败");
        }

        // 刷新班级人数
        this.flushStudentCount(classId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteSingleStudentFromClass(Long classId, Long studentId) {
        // 根据student_id查询student_number
        Student student = studentMapper.selectOne(new QueryWrapper<Student>()
                .eq("id", studentId));
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        String studentNumber = student.getStudentNumber();

        // 删除中间表记录
        int i = studentClassMapper.delete(new QueryWrapper<StudentClass>()
                .eq("student_number", studentNumber)
                .eq("class_id", classId));
        if (i == 0) {
            throw new RuntimeException("删除失败");
        }

        // 刷新班级人数
        this.flushStudentCount(classId);
        return true;
    }

    @Override
    @Transactional
    public boolean importStudents(Long classId, MultipartFile file) throws IOException {
        TeachClass teachClass = this.getById(classId);
        if (teachClass == null) {
            throw new RuntimeException("教学班级不存在");
        }

        // 读取 Excel 文件
        List<StudentExcelDTO> studentList = ExcelUtil.readExcel(file, StudentExcelDTO.class);

        // 转换 Student 实体并保存到数据库
        List<Student> newStudents = studentList.stream()
                .map(dto -> BeanUtil.copyProperties(dto, Student.class))
                .collect(Collectors.toList());
        studentService.saveBatch(newStudents);

        // 更新关联关系到中间表 student_class
        List<StudentClass> studentClasses = newStudents.stream()
                .map(student -> new StudentClass(student.getStudentNumber(), classId))
                .collect(Collectors.toList());
        studentClassService.saveBatch(studentClasses);

        // 更新教学班级的人数
        flushStudentCount(classId);
        return true;
    }

    @Override
    public boolean addTeachClass(TeachClassDTO teachClassDTO) {
        return save(BeanUtil.copyProperties(teachClassDTO, TeachClass.class));
    }

    @Override
    public TeachClassVO getTeachClassById(Long id) {
        TeachClass teachClass = getById(id);
        if (teachClass == null) {
            throw new RuntimeException("教学班级不存在");
        }
        TeachClassVO teachClassVO = BeanUtil.copyProperties(teachClass, TeachClassVO.class);
        teachClassVO.setSemester(semesterMapper.selectById(teachClass.getSemesterId()));
        return teachClassVO;
    }

    @Override
    public List<TeachClassVO> getTeachClassByTeacherId(Long teacherId) {
        List<TeachClass> teachClasses = list(new QueryWrapper<TeachClass>().eq("teacher_id", teacherId));
        ArrayList<TeachClassVO> teachClassesVOS = new ArrayList<>();
        teachClasses.forEach(teachClass -> {
            TeachClassVO teachClassVO = BeanUtil.copyProperties(teachClass, TeachClassVO.class);
            teachClassVO.setSemester(semesterMapper.selectById(teachClass.getSemesterId()));
            teachClassesVOS.add(teachClassVO);
        });
        return teachClassesVOS;
    }

    @Override
    public List<TeachClassVO> getTeachClassByStudentId(Long studentId) {
        List<StudentClass> studentClasses = studentClassMapper.selectList(new QueryWrapper<StudentClass>().eq("student_id", studentId));
        ArrayList<TeachClassVO> teachClassVOS = new ArrayList<>();
        studentClasses.forEach(studentClass -> {
            TeachClassVO teachClassVO = BeanUtil.copyProperties(getById(studentClass.getClassId()), TeachClassVO.class);
            teachClassVO.setSemester(semesterMapper.selectById(getById(studentClass.getClassId()).getSemesterId()));
            teachClassVOS.add(teachClassVO);
        });
        return teachClassVOS;
    }

    // endregion
}
