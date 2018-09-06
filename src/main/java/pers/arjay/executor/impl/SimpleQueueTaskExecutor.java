package pers.arjay.executor.impl;

import java.util.Collection;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;
import pers.arjay.executor.MultipleQueueTaskExecutor;

@Slf4j
public class SimpleQueueTaskExecutor implements MultipleQueueTaskExecutor {

	private int poolSize = 8;

	public ThreadPoolTaskExecutor[] threadPoolTasks;

	public SimpleQueueTaskExecutor() {
		log.info("initial SimpleTaskQueueExecutor...");
		log.info("SimpleTaskQueueExecutor pool size :{}", poolSize);

		threadPoolTasks = new ThreadPoolTaskExecutor[poolSize];
		for (int i = 0; i < poolSize; i++) {
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(1);
			executor.setMaxPoolSize(1);
			executor.setKeepAliveSeconds(60);
			executor.setQueueCapacity(Integer.MAX_VALUE);
			executor.setAwaitTerminationSeconds(10);
			executor.setWaitForTasksToCompleteOnShutdown(true);
			executor.setBeanName("SimpleTaskQueueExecutor " + i);
			executor.initialize();
			threadPoolTasks[i] = executor;
		}
	}

	@Override
	public ThreadPoolTaskExecutor getExcutorByUserId(Long userId) {
		return threadPoolTasks[userId.intValue() % poolSize];
	}
	
	public void close() throws InterruptedException {
		log.info("shutdowning executors...");
		for (int i = 0; i < poolSize; i++) {
			int tryTimes = 0;
			while (threadPoolTasks[i].getActiveCount() != 0 && tryTimes++ < 10) {
				Thread.sleep(1000);
			}
			threadPoolTasks[i].destroy();
		}
	}

	@Override
	public ThreadPoolTaskExecutor getExcutorByUserIds(Collection<Long> userIds) {
		return threadPoolTasks[Math.abs((int)userIds.stream().mapToLong(user->user.longValue()).sum()) % poolSize];
	}

}
