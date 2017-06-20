package com.xmd.technician.permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class BusinessPermissionAspect {
    private IBusinessPermissionManager pm = BusinessPermissionManager.getInstance();

    @Around("execution(* *(..)) && @annotation(com.xmd.technician.permission.CheckBusinessPermission)")
    public void checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//        for (Method method : joinPoint.getTarget().getClass().getMethods()) {
//            if (method.getName().equals(methodName)) {
//                CheckBusinessPermission annotation = method.getAnnotation(CheckBusinessPermission.class);
//                if (pm.containPermission(annotation.value())) {
//                    joinPoint.proceed();
//                }
//                break;
//            }
//        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());
        CheckBusinessPermission annotation = method.getAnnotation(CheckBusinessPermission.class);
        if (pm.containPermission(annotation.value())) {
            joinPoint.proceed();
        }
    }
}
