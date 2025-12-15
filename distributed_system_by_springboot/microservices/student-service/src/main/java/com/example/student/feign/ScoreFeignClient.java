package com.example.student.feign;

import com.example.common.vo.StudentScoreDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 成绩服务Feign客户端
 */
@FeignClient(name = "score-service", url = "${service.score-url:http://localhost:8085}")
public interface ScoreFeignClient {
    
    /**
     * 获取学生成绩详情
     */
    @GetMapping("/scores/student/{studentId}/detail")
    StudentScoreDetailVO getStudentScoreDetail(@PathVariable("studentId") String studentId);
}
