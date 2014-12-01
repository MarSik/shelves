package org.marsik.elshelves.backend.app.spring;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * ServletRequestDataBinder which supports fields renaming using {@link ParamName}
 *
 * @author jkee
 */
public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {

    private final Map<String, String> renameMapping;

    public ParamNameDataBinder(Object target, String objectName, Map<String, String> renameMapping) {
        super(target, objectName);
        this.renameMapping = renameMapping;
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);

        if (renameMapping.isEmpty()) return;

        for (PropertyValue pv: mpvs.getPropertyValues()) {
            if (renameMapping.containsKey(pv.getName())) {
                mpvs.add(renameMapping.get(pv.getName()), pv.getValue());
            }

            if (renameMapping.containsKey(pv.getName().toLowerCase())) {
                mpvs.add(renameMapping.get(pv.getName().toLowerCase()), pv.getValue());
            }
        }
    }
}
