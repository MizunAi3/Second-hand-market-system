package com.market.modules.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.modules.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付流水 Mapper
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
