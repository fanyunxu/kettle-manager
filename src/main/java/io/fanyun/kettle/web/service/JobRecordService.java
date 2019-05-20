package io.fanyun.kettle.web.service;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.mapper.KJobRecordDao;
import io.fanyun.kettle.core.model.po.KJobRecord;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class JobRecordService {

	@Autowired
	private KJobRecordDao kJobRecordDao;
	
	/**
	 * @Title getList
	 * @Description 获取带分页的列表
	 * @param uId 用户ID
	 * @param jobId 作业ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(BaseWhere where, Integer uId, Integer jobId){
		KJobRecord template = new KJobRecord();
		template.setAddUser(uId);
		if (jobId != null){
			template.setRecordJob(jobId);
		}
		Page page= PageHelper.startPage(where.getPage(),where.getPageSize());
		 kJobRecordDao.select(template);
		return new BootTablePage(page);
	}
	
	/**
	 * @Title getLogContent
	 * @Description 转换日志内容
	 * @return
	 * @throws IOException
	 * @return String
	 */
	public String getLogContent(Integer jobId) throws IOException {
		KJobRecord kJobRecord = kJobRecordDao.selectByPrimaryKey(jobId);
		String logFilePath = kJobRecord.getLogFilePath();
		return FileUtils.readFileToString(new File(logFilePath), Constant.DEFAULT_ENCODING);
	}
}