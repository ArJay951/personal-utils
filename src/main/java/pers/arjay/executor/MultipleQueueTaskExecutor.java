package pers.arjay.executor;

import java.util.Collection;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public interface MultipleQueueTaskExecutor {

	public ThreadPoolTaskExecutor getExcutorByUserId(Long userId);
	
	public ThreadPoolTaskExecutor getExcutorByUserIds(Collection<Long> userIds);

	public void close() throws InterruptedException;

}
