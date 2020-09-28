package com.springmq.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
//xml文件中的根标识
@XmlRootElement(name = "Product")
//控制jaxb绑定类中属性和字段的排序
@XmlType(propOrder = {
        "id",
        "productNo",
        "total",
        "createTime",
        "updateTime",
})
public class Product implements Serializable {
   private Integer id;
   private  String productNo;
   private  Integer total;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
   private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
   private Date updateTime;
}
