package com.magicbeans.happygo.task;

import com.magicbeans.happygo.entity.IncomeDetail;
import com.magicbeans.happygo.entity.SystemConfig;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.mapper.IUserMapper;
import com.magicbeans.happygo.mapper.IncomeDetailMapper;
import com.magicbeans.happygo.mapper.SystemConfigMapper;
import com.magicbeans.happygo.service.ISystemConfigService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 计算收益任务 通过积分兑换欢喜券
 * 每天凌晨00点30执行
 * Created by Eric Xie on 2018/2/5 0005.
 */
@Component
public class CountIncomeTask {

    @Resource
    private IUserMapper userMapper;
    @Resource
    private ISystemConfigService systemConfigService;
    @Resource
    private IncomeDetailMapper incomeDetailMapper;

    @Scheduled(cron = "0 30 0 * * ?")
    public void countIncome(){
        List<User> users = userMapper.queryAllUser();
        if(null != users && users.size() > 0){
            SystemConfig config = systemConfigService.findAll().get(0);
            // 计算今日所得欢喜券
            for (User user : users) {
                if(null != user.getScore() && user.getScore() > 0){
                    IncomeDetail detail = new IncomeDetail();
                    detail.setToUserId(user.getId());
                    detail.setType(1);
                    detail.setBigDecimal(new BigDecimal(user.getScore() * config.getParities() / 100));
                    incomeDetailMapper.insert(detail);
                }
            }
        }
    }
}
