package micromix.boot.spring;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test bean referencing some other bean.
 */
public class BeanHolder {

    @Autowired
    Bean bean;

}
