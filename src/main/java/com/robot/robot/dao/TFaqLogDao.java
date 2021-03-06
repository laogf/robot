package com.robot.robot.dao;

import com.robot.robot.domain.TFaqLogDO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 问答记录表
 *
 * @author laoGF
 * @date 2018-08-09 19:03:39
 */
@Mapper
public interface TFaqLogDao {

	TFaqLogDO get(Long id);
	
	List<TFaqLogDO> list(Map<String,Object> map);
	
	int count(Map<String,Object> map);
	
	int save(TFaqLogDO tFaqLog);
	
	int update(TFaqLogDO tFaqLog);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);

	List<TFaqLogDO> statistics(Map<String,Object> map);

	List<TFaqLogDO> all(Map<String,Object> map);
}
