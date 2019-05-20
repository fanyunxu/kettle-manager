package io.fanyun.kettle.common.kettle.environment;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartInit implements ApplicationListener<ApplicationStartedEvent> {


	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
		try {
			KettleEnvironment.init();
			KettleInit.init();
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}
}
