package io.github.ithamal.beanfetch.fetcher.enhance;

import io.github.ithamal.beanfetch.fetcher.FetchCallback;
import io.github.ithamal.beanfetch.fetcher.ValueSetter;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-09-20 13:32
 */
public class BeanEnhancer {

    @SneakyThrows
    public static Object enhanceSingle(ValueSetter<?, ?> setter, FetchCallback<?, ?> fetchCallback) {
        ProxyInfo proxyInfo = getProxyInfo(setter);
        return Enhancer.create(proxyInfo.valueClass, (MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getName().startsWith("get")) {
                Object source = fetchCallback.fetch();
                Object target = proxyInfo.refGetter.invoke(source);
                if(target != null) {
                    return method.invoke(target, args);
                }
            }
            return proxy.invokeSuper(obj, args);
        });
    }

    @SneakyThrows
    public static Object enhanceMany(ValueSetter<?, ?> setter, FetchCallback<?,?> fetchCallback) {
        ProxyInfo proxyInfo = getProxyInfo(setter);
        if(proxyInfo.valueClass == List.class){
            return new ListProxy<>(fetchCallback, proxyInfo.refGetter);
        }
        return Enhancer.create(proxyInfo.valueClass, (MethodInterceptor) (obj, method, args, proxy) -> {
            Object source = fetchCallback.fetch();
            Object target = proxyInfo.refGetter.invoke(source);
            if(target != null) {
                return method.invoke(target, args);
            }
            return proxy.invokeSuper(obj, args);
        });
    }

    @SneakyThrows
    public static ProxyInfo getProxyInfo(ValueSetter<?, ?> setter) {
        ProxyInfo methodInfo = new ProxyInfo();
        SerializedLambda lambda = getLambda(setter);
        String setterMethodName = lambda.getImplMethodName();
        String getMethodName = setterMethodName.replaceFirst("set", "get");
        String refClassName = lambda.getInstantiatedMethodType().split(";")[0].substring(2).replace("/", ".");
        String valueClassName = lambda.getInstantiatedMethodType().split(";")[1].substring(1).replace("/", ".");
        methodInfo.refClass = Class.forName(refClassName);
        methodInfo.valueClass = Class.forName(valueClassName);
        methodInfo.refGetter = methodInfo.refClass.getDeclaredMethod(getMethodName);
        return methodInfo;
    }

    @SneakyThrows
    private static SerializedLambda getLambda(ValueSetter<?, ?> setter) {
        Method method = setter.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(setter);
    }

    public static class ProxyInfo {

        private Method refGetter;

        private Class<?> refClass;

        private Class<?> valueClass;
    }

}
