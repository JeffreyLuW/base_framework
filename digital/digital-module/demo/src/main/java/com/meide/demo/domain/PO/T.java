package com.meide.demo.domain.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * taos测试用
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class T {

    Timestamp ts;
    Integer speed;

}
