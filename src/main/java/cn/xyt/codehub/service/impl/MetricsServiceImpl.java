package cn.xyt.codehub.service.impl;

import cn.xyt.codehub.service.MetricsService;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Service
public class MetricsServiceImpl implements MetricsService {
    SystemInfo si = new SystemInfo();
    public double[] getCpuLoad() {
        CentralProcessor processor = si.getHardware().getProcessor();
        return processor.getProcessorCpuLoad(1000);
    }

    @Override
    public double getMemoryLoad() {
        GlobalMemory memory = si.getHardware().getMemory();

        // 获取总内存和已用内存
        long totalMemory = memory.getTotal();
        long usedMemory = memory.getTotal() - memory.getAvailable();

        // 计算内存占用率
        return ((double) usedMemory / totalMemory) * 100;
    }
}
