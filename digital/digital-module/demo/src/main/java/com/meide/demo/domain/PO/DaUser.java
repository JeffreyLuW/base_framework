package com.meide.demo.domain.PO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* entity
* 
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaUser implements Serializable {
    /**
      *
      **/
    private Integer userId;

        /**
      * 
      **/
    private String userName;

        /**
      * 
      **/
    private String userPassword;

    }