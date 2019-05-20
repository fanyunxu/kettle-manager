package io.fanyun.kettle.web.quartz;

import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.mapper.KJobDao;
import io.fanyun.kettle.core.mapper.KTransDao;
import io.fanyun.kettle.core.model.po.KJob;
import io.fanyun.kettle.core.model.po.KTrans;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuartzListener implements JobListener{
	@Autowired
	private KJobDao kJobDao;
	@Autowired
	private KTransDao kTransDao;

	private QuartzManager quartzManager;
	@Autowired
	public void setQuartzManager(QuartzManager quartzManager) {
		this.quartzManager = quartzManager;
	}

	@Override
	public String getName() {
		return System.currentTimeMillis() + "QuartzListener";
	}
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	}
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
	}
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		String jobName = context.getJobDetail().getKey().getName();
		String jobGroupName = context.getJobDetail().getKey().getGroup();
		String triggerName = context.getTrigger().getKey().getName();
		String triggerGroupName = context.getTrigger().getKey().getGroup();
		//一次性任务，执行完之后需要移除
		quartzManager.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Integer jobId = Integer.valueOf(String.valueOf(dataMap.get(Constant.TRANSID)));
		String jobtype = String.valueOf(dataMap.get(Constant.JOBTYPE));

		//Object DbConnectionObject = dataMap.get(Constant.DBCONNECTIONOBJECT);
		//DBConnectionModel DBConnectionModel = (DBConnectionModel) DbConnectionObject;
		if (jobtype.equals("1")){
			KJob kJob = kJobDao.selectByPrimaryKey(jobId);
			kJob.setJobStatus(2);
			kJobDao.updateByPrimaryKeySelective(kJob);
		}
		else if (jobtype.equals("2")) {
			KTrans kTrans = kTransDao.selectByPrimaryKey(jobId);
			kTrans.setTransStatus(2);
			kTransDao.updateByPrimaryKeySelective(kTrans);
		}
	}
}
