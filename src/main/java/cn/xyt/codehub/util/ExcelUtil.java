package cn.xyt.codehub.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), clazz, new ReadListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                dataList.add(data); // 收集每一行的数据
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 全部解析完成的回调
            }
        }).sheet().doRead();
        return dataList;
    }
}
