package com.xmd.permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class BusinessPermissionAspect {
    private IBusinessPermissionManager pm = BusinessPermissionManager.getInstance();

    @Around("execution(* *(..)) && @annotation(com.xmd.permission.CheckBusinessPermission)")
    public void checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());
        CheckBusinessPermission annotation = method.getAnnotation(CheckBusinessPermission.class);
        if (pm.containPermissions(annotation.value())) {
            joinPoint.proceed();
        }
    }
}
