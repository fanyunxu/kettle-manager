package com.cmct.kettle.core.mapper;

import com.cmct.kettle.core.model.po.KTrans;
import com.cmct.kettle.core.model.vo.TransVo;
import com.cmct.kettle.tkmybatisconf.BaseMapper;

import java.util.List;


public interface KTransDao extends BaseMapper<KTrans> {
    /**
     * 获取转换任务列表
     * @param template
     * @return
     */
    List<TransVo> getListByTrans(KTrans template);
}