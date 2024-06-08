package com.atguigu.cloud.apis;

import com.atguigu.cloud.entittes.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(value = "cloud-payment-service")
@FeignClient(value = "cloud-gateway")
public interface PayFeignApi {

    /**
     * 新增一条支付相关的流水记录
     * @return
     */
    @PostMapping("/pay/add")
    public ResultData addOrder(@RequestBody PayDTO payDTO);

    /**
     * 按照主键添加流水记录
     * @param id
     * @return
     */
    @GetMapping("/pay/get/{id}")
    public ResultData getById(@PathVariable("id") Integer id);

    /**
     * openFeign天然支持负载均衡演示
     * @return
     */
    @GetMapping("/pay/get/info")
    public String myLb();

    @GetMapping("/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/bulkhead/{id}")
    public String myBulkhead(@PathVariable("id") Integer id);

    //=========Resilience4j ratelimit 的例子
    @GetMapping(value = "/pay/ratelimit/{id}")
    public String myRatelimit(@PathVariable("id") Integer id);

    /**
     * Micrometer(Sleuth)进行链路监控的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/micrometer/{id}")
    public String myMicrometer(@PathVariable("id") Integer id);

    /**
     * GateWay进行网关测试案例01
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/gateway/get/{id}")
    public ResultData getByIdGateway(@PathVariable("id") Integer id);

    /**
     * GateWay进行网关测试案例02
     * @return
     */
    @GetMapping(value = "/pay/gateway/info")
    public ResultData<String> getGatewayInfo();

    /**
     * GateWay过滤器测试
     * @return
     */
    @GetMapping(value = "/pay/gateway/filter")
    public ResultData<String> getGatewayFilter();

}
