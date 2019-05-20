package io.fanyun.kettle.web.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataValidate {

	/**
	 * @Title AllNotEmpty
	 * @Description 实体类参数校验（写这个方法的时候我的内心是崩溃的）
	 * @param object 实体类
	 * @return
	 * @return boolean
	 */
	public static boolean AllNotEmpty(Object object){
		List<Object> returnList = new ArrayList<>();
		try {
			Map<?, ?> describe = BeanUtils.describe(object);
			describe.forEach((k, v) -> {
				if (ObjectUtils.isEmpty(v)){
					returnList.add(v);
				}
			});
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return CollectionUtils.isEmpty(returnList);
	}
	

}
