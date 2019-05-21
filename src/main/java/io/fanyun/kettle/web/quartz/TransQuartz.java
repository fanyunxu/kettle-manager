package io.fanyun.kettle.web.quartz;

import io.fanyun.kettle.common.kettle.repository.RepositoryUtil;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.mapper.KTransMonitorDao;
import io.fanyun.kettle.core.mapper.KTransRecordDao;
import io.fanyun.kettle.core.model.po.KRepository;
import io.fanyun.kettle.core.model.po.KTransMonitor;
import io.fanyun.kettle.core.model.po.KTransRecord;
import io.fanyun.kettle.web.quartz.model.DBConnectionModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
@Component
@EnableScheduling
public class TransQuartz implements Job {

	private KTransRecordDao kTransRecordDao;

	private KTransMonitorDao kTransMonitorDao;
	@Autowired
	public void setkTransRecordDao(KTransRecordDao kTransRecordDao) {
		this.kTransRecordDao = kTransRecordDao;
	}
	@Autowired
	public void setkTransMonitorDao(KTransMonitorDao kTransMonitorDao) {
		this.kTransMonitorDao = kTransMonitorDao;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap transDataMap = context.getJobDetail().getJobDataMap();
		Object kRepositoryObject = transDataMap.get(Constant.REPOSITORYOBJECT);
		Object dbConnectionObject = transDataMap.get(Constant.DBCONNECTIONOBJECT);
		String transId = String.valueOf(transDataMap.get(Constant.TRANSID));
		String transPath = String.valueOf(transDataMap.get(Constant.TRANSPATH));
		String transName = String.valueOf(transDataMap.get(Constant.TRANSNAME));
		String userId = String.valueOf(transDataMap.get(Constant.USERID));
		String logLevel = String.valueOf(transDataMap.get(Constant.LOGLEVEL));
		String logFilePath = String.valueOf(transDataMap.get(Constant.LOGFILEPATH));
		// 首先判断数据库连接对象是否正确
		if (dbConnectionObject instanceof DBConnectionModel) {
			// 判断转换类型
			// 证明该转换是从资源库中获取到的
			if (kRepositoryObject instanceof KRepository) {
				try {
					runRepositorytrans(kRepositoryObject, dbConnectionObject, transId, transPath, transName, userId,
							logLevel, logFilePath);
				} catch (KettleException e) {
					e.printStackTrace();
				}
			} else {
				try {
					runFiletrans(dbConnectionObject, transId, transPath, transName, userId, logLevel, logFilePath);
				} catch (KettleXMLException | KettleMissingPluginsException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Title runRepositorytrans
	 * @Description 运行资源库中的转换
	 * @param kRepositoryObject
	 *            数据库连接对象
	 * @param dbConnectionObject
	 *            资源库对象
	 * @param transId
	 *            转换ID
	 * @param transPath
	 *            转换在资源库中的路径信息
	 * @param transName
	 *            转换名称
	 * @param userId
	 *            转换归属者ID
	 * @param logLevel
	 *            转换的日志等级
	 * @param logFilePath
	 *            转换日志保存的根路径
	 * @throws KettleException
	 * @return void
	 */
	private void runRepositorytrans(Object kRepositoryObject, Object dbConnectionObject, String transId,
			String transPath, String transName, String userId, String logLevel, String logFilePath)
			throws KettleException {
		KRepository kRepository = (KRepository) kRepositoryObject;
		Integer repositoryId = kRepository.getRepositoryId();
		KettleDatabaseRepository kettleDatabaseRepository = null;
		if (RepositoryUtil.KettleDatabaseRepositoryCatch.containsKey(repositoryId)) {
			kettleDatabaseRepository = RepositoryUtil.KettleDatabaseRepositoryCatch.get(repositoryId);
		} else {
			kettleDatabaseRepository = RepositoryUtil.connectionRepository(kRepository);
		}
		if (null != kettleDatabaseRepository) {
			RepositoryDirectoryInterface directory = kettleDatabaseRepository.loadRepositoryDirectoryTree()
					.findDirectory(transPath);
			TransMeta transMeta = kettleDatabaseRepository.loadTransformation(transName, directory,
					new ProgressNullMonitorListener(), true, null);
			Trans trans = new Trans(transMeta);
			trans.setLogLevel(LogLevel.DEBUG);
			if (StringUtils.isNotEmpty(logLevel)) {
				trans.setLogLevel(Constant.logger(logLevel));
			}
			String exception = null;
			Integer recordStatus = 1;
			Date transStartDate = null;
			Date transStopDate = null;
			String logText = null;
			try {
				transStartDate = new Date();
				trans.execute(null);
				trans.waitUntilFinished();
				transStopDate = new Date();
			} catch (Exception e) {
				exception = e.getMessage();
				recordStatus = 2;
			} finally {
				if (trans.isFinished()) {
					if (trans.getErrors() > 0
							&& (null == trans.getResult().getLogText() || "".equals(trans.getResult().getLogText()))) {
						logText = exception;
					}
					// 写入转换执行结果
					StringBuilder allLogFilePath = new StringBuilder();
					allLogFilePath.append(logFilePath).append("/").append(userId).append("/")
							.append(StringUtils.remove(transPath, "/")).append("@").append(transName).append("-log")
							.append("/").append(System.currentTimeMillis()).append(".").append("txt");
					String logChannelId = trans.getLogChannelId();
					LoggingBuffer appender = KettleLogStore.getAppender();
					logText = appender.getBuffer(logChannelId, true).toString();
					try {
						KTransRecord kTransRecord = new KTransRecord();
						kTransRecord.setRecordTrans(Integer.parseInt(transId));
						kTransRecord.setLogFilePath(allLogFilePath.toString());
						kTransRecord.setAddUser(Integer.parseInt(userId));
						kTransRecord.setRecordStatus(recordStatus);
						kTransRecord.setStartTime(transStartDate);
						kTransRecord.setStopTime(transStopDate);
						writeToDBAndFile(dbConnectionObject, kTransRecord, logText);
					} catch (IOException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @Title runFiletrans
	 * @Description 运行文件类型的转换
	 * @param DbConnectionObject
	 *            数据库连接对象
	 * @param transId
	 *            装换ID
	 * @param transPath
	 *            转换文件所在的路径
	 * @param transName
	 *            转换的名称
	 * @param userId
	 *            用户ID
	 * @param logLevel
	 *            装换运行的日志等级
	 * @param logFilePath
	 *            转换的运行日志保存的位置
	 * @throws KettleXMLException
	 * @throws KettleMissingPluginsException
	 * @return void
	 */
	private void runFiletrans(Object DbConnectionObject, String transId, String transPath, String transName,
			String userId, String logLevel, String logFilePath)
			throws KettleXMLException, KettleMissingPluginsException {
		TransMeta transMeta = new TransMeta(transPath);
		Trans trans = new Trans(transMeta);
		trans.setLogLevel(LogLevel.DEBUG);
		if (StringUtils.isNotEmpty(logLevel)) {
			trans.setLogLevel(Constant.logger(logLevel));
		}
		String exception = null;
		Integer recordStatus = 1;
		Date transStartDate = null;
		Date transStopDate = null;
		String logText = null;
		try {
			transStartDate = new Date();
			trans.execute(null);
			trans.waitUntilFinished();
			transStopDate = new Date();
		} catch (Exception e) {
			exception = e.getMessage();
			recordStatus = 2;
		} finally {
			if (null != trans && trans.isFinished()) {
				if (trans.getErrors() > 0
						&& (null == trans.getResult().getLogText() || "".equals(trans.getResult().getLogText()))) {
					logText = exception;
				}
				// 写入转换执行结果
				StringBuilder allLogFilePath = new StringBuilder();
				allLogFilePath.append(logFilePath).append("/").append(userId).append("/")
						.append(StringUtils.remove(transPath, "/")).append("@").append(transName).append("-log")
						.append("/").append(new Date().getTime()).append(".").append("txt");
				String logChannelId = trans.getLogChannelId();
				LoggingBuffer appender = KettleLogStore.getAppender();
				logText = appender.getBuffer(logChannelId, true).toString();
				try {
			  		KTransRecord kTransRecord = new KTransRecord();
					kTransRecord.setRecordTrans(Integer.parseInt(transId));
					kTransRecord.setAddUser(Integer.parseInt(userId));
					kTransRecord.setLogFilePath(allLogFilePath.toString());
					kTransRecord.setRecordStatus(recordStatus);
					kTransRecord.setStartTime(transStartDate);
					kTransRecord.setStopTime(transStopDate);
					writeToDBAndFile(DbConnectionObject, kTransRecord, logText);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Title writeToDBAndFile
	 * @Description 保存转换运行日志信息到文件和数据库
	 * @param DbConnectionObject 数据库连接对象
	 * @param kTransRecord 转换运行记录对象
	 * @param logText 日志记录
	 * @throws IOException 
	 * @throws SQLException
	 * @return void
	 */
	private void writeToDBAndFile(Object DbConnectionObject, KTransRecord kTransRecord, String logText)
			throws IOException, SQLException {
		// 将日志信息写入文件
		FileUtils.writeStringToFile(new File(kTransRecord.getLogFilePath()), logText, Constant.DEFAULT_ENCODING, false);

 		kTransRecordDao.insert(kTransRecord);
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(kTransRecord.getAddUser());
		template.setMonitorTrans(kTransRecord.getRecordTrans());
		KTransMonitor templateOne = kTransMonitorDao.selectOne(template);
		if(kTransRecord.getRecordStatus() == 1){// 证明成功
			//成功次数加1
			templateOne.setMonitorSuccess(templateOne.getMonitorSuccess() + 1);
			kTransMonitorDao.updateByPrimaryKeySelective(templateOne);
		}else if (kTransRecord.getRecordStatus() == 2){// 证明失败
			//失败次数加1
			templateOne.setMonitorFail(templateOne.getMonitorFail() + 1);
			kTransMonitorDao.updateByPrimaryKeySelective(templateOne);
		}
	}
}