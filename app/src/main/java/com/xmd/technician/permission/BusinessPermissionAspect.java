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
//        Logger.d(joinPoint.getSignature().toString());
        String methodName = joinPoint.getSignature().getName();
//        Logger.d("class:" + joinPoint.getTarget().getClass().getName() + ",method:" + methodName);
        for (Method method : joinPoint.getTarget().getClass().getMethods()) {
//            Logger.d("loop " + method.getName());
            if (method.getName().equals(methodName)) {
                CheckBusinessPermission annotation = method.getAnnotation(CheckBusinessPermission.class);
                Logger.d("check " + annotation.value()[0] + " on " + method.getName());
                if (pm.containPermission(annotation.value())) {
                    Logger.d("pass");
                    joinPoint.proceed();
                } else {
                    Logger.d("refuse");
                }
                break;
            }
        }
    }
}
