package com.sishuok.es.maintain.dynamictask.service;

import com.sishuok.es.maintain.dynamictask.entity.TaskDefinition;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: DynamicTaskApi.java, v 0.1 2014年11月19日 下午3:04:55 kejun.song Exp $
 */
public interface DynamicTaskApi {

    public void addTaskDefinition(TaskDefinition taskDefinition);

    public void updateTaskDefinition(TaskDefinition taskDefinition);

    public void deleteTaskDefinition(boolean forceTermination, Long... taskDefinitionIds);

    public void startTask(Long... taskDefinitionIds);

    public void stopTask(boolean forceTermination, Long... taskDefinitionId);

}
