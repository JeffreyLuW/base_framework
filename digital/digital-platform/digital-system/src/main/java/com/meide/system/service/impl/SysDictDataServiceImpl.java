package com.meide.system.service.impl;

import com.meide.common.core.domain.entity.SysDictData;
import com.meide.common.utils.SysDictDetail;
import com.meide.system.mapper.SysDictDataMapper;
import com.meide.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author jiay
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "sys_dict", allEntries = true),
            @CacheEvict(value = "sys_dict_details", allEntries = true)
    })
    public int deleteDictDataByIds(Long[] dictCodes) {
        return dictDataMapper.deleteDictDataByIds(dictCodes);
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "sys_dict", allEntries = true),
            @CacheEvict(value = "sys_dict_details", allEntries = true)
    })
    public int insertDictData(SysDictData dictData) {
        return dictDataMapper.insertDictData(dictData);
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "sys_dict", allEntries = true),
            @CacheEvict(value = "sys_dict_details", allEntries = true)
    })
    public int updateDictData(SysDictData dictData) {
        return dictDataMapper.updateDictData(dictData);
    }

    @Override
    public List<SysDictData> findAll() {
        return dictDataMapper.findAll();
    }

    @Override
    @Cacheable(value = "sys_dict_details")
    public List<SysDictDetail> findAllDetail() {
        List<SysDictData> list = this.findAll();
        if (null == list) {
            return new ArrayList<>();
        }
        return list.stream().map(sysDictData ->
                new SysDictDetail(sysDictData.getDictType(), sysDictData.getDictLabel(), sysDictData.getDictValue()))
                .collect(Collectors.toList());
    }
}
