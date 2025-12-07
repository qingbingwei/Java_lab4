package annotation;

import model.Role.RoleLevel;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色级别检查注解
 * 用于标记需要特定角色级别才能执行的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    /**
     * 需要的最低角色级别
     */
    RoleLevel value();
    
    /**
     * 角色描述
     */
    String description() default "";
}
