package com.springmq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmq.dao.ProductMapper;
import com.springmq.dao.ProductRecordMapper;
import com.springmq.entity.Product;
import com.springmq.entity.ProductRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConcurrencyServiceImpl implements ConcurrencyService {
    private static final Logger log = LoggerFactory.getLogger(ConcurrencyServiceImpl.class);
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRecordMapper productRecordMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectMapper objectMapper;
    //数据库写死的数据
    private static final String productNo = "product_123456";
    @Override
    public void manageRobbing(String mobile) {
        try {
            //通过商品编号查询消息
            Product product = productMapper.selectByproductNo(productNo);
            //判断不为空且商品数量必须大于0情况下
            if(product!=null && product.getTotal()>0){
                productMapper.updateTotal(productNo);
                ProductRecord po = new ProductRecord();
                po.setMobile(mobile);
                po.setProductId(product.getId());
                productRecordMapper.insertProductRecord(po);
            }
        } catch (Exception e) {
            log.error("处理抢单发生异常 mobile={},err={}",mobile,e.fillInStackTrace());

        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Product> findAll() {
        return productMapper.findAll();
    }
}
