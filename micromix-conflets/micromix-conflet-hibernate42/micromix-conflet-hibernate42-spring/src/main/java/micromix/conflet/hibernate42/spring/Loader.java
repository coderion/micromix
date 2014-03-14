package micromix.conflet.hibernate42.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;

public class Loader {

    DisposableBean disposableBean;
    ReflectUtils reflectUtils;
    MethodProxy methodProxy;
    FastClass fastClass;

}
