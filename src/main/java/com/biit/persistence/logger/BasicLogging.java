package com.biit.persistence.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StopWatch;

/**
 * Logs all file managed by Spring. In this project only are DAOs.
 * 
 */
@Aspect
public class BasicLogging extends AbstractLogging {

	@Around("selectAll()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		Object returnValue = null;
		stopWatch.start();
		returnValue = joinPoint.proceed();
		stopWatch.stop();
		log(stopWatch.getTotalTimeMillis(), joinPoint);
		return returnValue;
	}
}
