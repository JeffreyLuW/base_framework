package com.meide.demo.domain.VO;

import com.meide.demo.domain.PO.DeviceGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceGroupVo extends DeviceGroup {

    List<DeviceGroupVo> children;


}
