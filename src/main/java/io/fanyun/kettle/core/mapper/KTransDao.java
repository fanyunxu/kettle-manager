package io.fanyun.kettle.core.mapper;

import io.fanyun.kettle.core.model.po.KTrans;
import io.fanyun.kettle.core.model.vo.TransVo;
import io.fanyun.kettle.tkmybatisconf.BaseMapper;

import java.util.List;


public interface KTransDao extends BaseMapper<KTrans> {
    /**
     * 获取转换任务列表
     * @param template
     * @return
     */
    List<TransVo> getListByTrans(KTrans template);
}