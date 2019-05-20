package io.fanyun.kettle.web.service;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.mapper.KTransRecordDao;
import io.fanyun.kettle.core.model.po.KTransRecord;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;

@Service
public class TransRecordService {

	@Autowired
	private KTransRecordDao kTransRecordDao;
	
	/**
	 * @Title getList
	 * @Description 获取列表
	 * @param start 其实行数
	 * @param size 结束行数
	 * @param uId 用户ID
	 * @param transId 转换ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(BaseWhere where, Integer uId, Integer transId){
		KTransRecord template = new KTransRecord();
		template.setAddUser(uId);
		if (transId != null){
			template.setRecordTrans(transId);
		}
		Page page = PageHelper.startPage(where.getPage(),where.getPageSize());
		 kTransRecordDao.select(template);
		return new BootTablePage(page);
	}
	
	/**
	 * @Title getLogContent
	 * @Description 转换日志内容
	 * @param recordId 转换记录ID
	 * @return
	 * @throws IOException
	 * @return String
	 */
	public String getLogContent(Integer recordId) throws IOException{
		KTransRecord kTransRecord = kTransRecordDao.selectByPrimaryKey(recordId);
		String logFilePath = kTransRecord.getLogFilePath();
		return FileUtils.readFileToString(new File(logFilePath), Constant.DEFAULT_ENCODING);
	}
	
}