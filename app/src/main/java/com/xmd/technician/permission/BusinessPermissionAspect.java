package com.xmd.technician.permission;

import com.xmd.technician.common.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

@Aspect
public class BusinessPermissionAspect {
    private IBusinessPermissionManager pm = BusinessPermissionManager.getInstance();

    @Around("execution(* *(..)) && @annotation(com.xmd.technician.permission.CheckBusinessPermission)")
    public void checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger.v(joinPoint.getSignature().toString());
        String methodName = joinPoint.getSignature().getName();
        Logger.v("class:" + joinPoint.getTarget().getClass().getName() + ",method:" + methodName);
        for (Method method : joinPoint.getTarget().getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                CheckBusinessPermission annotation = method.getAnnotation(CheckBusinessPermission.class);
                Logger.v("check " + annotation.value()[0] + " on " + method.getName());
                if (pm.containPermission(annotation.value())) {
                    Logger.v("pass");
                    joinPoint.proceed();
                } else {
                    Logger.v("refuse");
                }
                break;
            }
        }
    }
}
