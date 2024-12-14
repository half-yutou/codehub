package cn.xyt.codehub;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.StudentExcelDTO;
import cn.xyt.codehub.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodehubApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void beanUtilTest() {
        StudentExcelDTO dto = new StudentExcelDTO();
        dto.setStudentNumber("001");
        dto.setUsername("aaa");
        dto.setAdminClass("001");
        dto.setGender("男");
        dto.setPhone("111");
        dto.setEmail("aaa");

        Student student = BeanUtil.copyProperties(dto, Student.class);
        System.out.println(student);
    }

}
