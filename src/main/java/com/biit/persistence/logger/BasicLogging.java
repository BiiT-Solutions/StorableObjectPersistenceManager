package com.biit.persistence.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

import com.biit.persistence.logger.AbstractLogging;

/**
 * Logs all file managed by Spring. In this project only are DAOs.
 * 
 */
@Aspect
public class BasicLogging extends AbstractLogging {

	/**
	 * Following is the definition for a pointcut to select all the methods available. So advice will be called for all
	 * the methods.
	 */
	@Pointcut("execution(* com.biit..*(..))")
	private void selectAll() {
	}

	/**
	 * This is the method which I would like to execute before a selected method execution.
	 */
	@Before("selectAll()")
	public void beforeAdvice() {
	}

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

	/**
	 * This is the method which I would like to execute after a selected method execution.
	 */
	@After("selectAll()")
	public void afterAdvice() {
	}

	/**
	 * This is the method which I would like to execute when any method returns.
	 */
	@AfterReturning(pointcut = "selectAll()", returning = "retVal")
	public void afterReturningAdvice(Object retVal) {
//		if (retVal != null) {
//			log("Returning:" + retVal.toString());
//		} else {
//			log("Returning:" + retVal);
//		}
	}

	/**
	 * This is the method which I would like to execute if there is an exception raised by any method.
	 */
	@AfterThrowing(pointcut = "selectAll()", throwing = "ex")
	public void AfterThrowingAdvice(IllegalArgumentException ex) {
//		log("There has been an exception: " + ex.getMessage());
	}

}
