package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.apis.AccountFeignApi;
import com.atguigu.cloud.apis.StorageFeignApi;
import com.atguigu.cloud.entities.Order;
import com.atguigu.cloud.mapper.OrderMapper;
import com.atguigu.cloud.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    // 订单微服务通过OpenFeign去调用库存微服务
    @Resource
    private StorageFeignApi storageFeignApi;
    @Autowired
    private AccountFeignApi accountFeignApi;

    // 订单微服务通过OpenFeign去调用账户微服务
    @Override
    @GlobalTransactional(name = "order-create", rollbackFor = Exception.class)
    public void create(Order order) {
        // xid 检查
        String xid = RootContext.getXID();
        // 1.新建订单
        log.info("============开始新建订单:\t xid:{}", xid);
        // 订单状态status：0:创建中，1:创建完成
        order.setStatus(0);
        int result = orderMapper.insert(order);

        //插入订单成功后获得插入MySQL的实体对象
        Order orderFromDb = null;
        if (result > 0) {
            orderFromDb = orderMapper.selectOne(order);
            log.info("=============新建订单信息成功:\t order:{}", orderFromDb.toString());
            System.out.println();
            // 2.扣除库存
            log.info("=============订单微服务开始调用storage库存，做扣减count");
            storageFeignApi.decrease(orderFromDb.getProductId(), orderFromDb.getCount());
            log.info("=============订单微服务结束调用storage库存，做扣减完成");
            System.out.println();
            // 3.扣减账户金额
            log.info("=============订单微服务开始调用Account账户，做扣减money");
            accountFeignApi.decrease(orderFromDb.getUserId(), orderFromDb.getMoney());
            log.info("=============订单微服务结束调用Account账户，做扣减完成");
            System.out.println();
            // 4.修改订单状态
            log.info("=============修改订单状态");
            orderFromDb.setStatus(1);
            Example whereCondition = new Example(Order.class);
            Example.Criteria criteria = whereCondition.createCriteria();
            criteria.andEqualTo("userId", orderFromDb.getUserId());
            criteria.andEqualTo("status", 0);
            int updateResult = orderMapper.updateByExampleSelective(orderFromDb, whereCondition);
            log.info("============修改订单状态完成");
            log.info("============orderFromDb:{}", orderFromDb.toString());
        }
        System.out.println();
        log.info("==============创建订单任务结束：\t xid:{}", xid);
    }
}
