package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.service.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/metrics")
@Tag(name = "监控信息")
public class MetricsController {

    @Resource
    private MetricsService metricsService;

    @Operation(summary = "获取CPU使用率")
    @GetMapping("/cpu")
    public Result getCpuLoad() {
        return Result.ok(metricsService.getCpuLoad());
    }

    @Operation(summary = "获取内存使用率")
    @GetMapping("/mem")
    public Result getMemoryLoad() {
        return Result.ok(metricsService.getMemoryLoad());
    }
}
