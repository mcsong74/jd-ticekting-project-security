package com.cybertek.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Configuration
public class LoggingAspect {

    Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.cybertek.controller.ProjectController.*(..)) || execution(* com.cybertek.controller" +
            ".TaskController.*(..))")
    private void anyControllerOperation(){}
    @Before("anyControllerOperation()")
    public void anyBeforeControllerOperationAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Before(User: {}, Method: {} - Parameters: {}", auth.getName(), joinPoint.getSignature(),
                joinPoint.getArgs().toString());
    }

}
