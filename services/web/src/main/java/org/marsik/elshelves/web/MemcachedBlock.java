package org.marsik.elshelves.web;


import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class MemcachedBlock implements TemplateDirectiveModel {
    static final String ID_PARAM = "id";
    static final int EXPIRY_TIME = 86400;

    final MemcachedClient memcachedClient;

    public MemcachedBlock(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public void execute(Environment env,
                        Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body)
            throws TemplateException, IOException {

        // Check if proper parameters were given
        if (params.isEmpty() || !params.containsKey(ID_PARAM)) {
            throw new TemplateModelException(
                    "This directive requires cache id.");
        }

        if (params.size() > 1) {
            throw new TemplateModelException(
                    "This directive requires only cache id and no extra arguments are supported.");
        }

        if (loopVars.length != 0) {
            throw new TemplateModelException(
                    "This directive doesn't allow loop variables.");
        }

        /* Check if the cache is valid */
        String id = (String) params.get(ID_PARAM);
        if (memcachedClient != null) {
            try {
                String cachedData = (String) memcachedClient.get(id);
                if (cachedData != null) {
                    env.getOut().write(cachedData);
                    return;
                }
            } catch (OperationTimeoutException e) {
                // ignore
            } catch (IllegalStateException e) {
                // ignore
            }
        }

        // If there is non-empty nested content:
        if (body != null) {
            // Executes the nested body. Same as <#nested> in FTL, except
            // that we use our own writer instead of the current output writer.
            if (memcachedClient != null) {
                StringWriter stringWriter = new StringWriter();
                body.render(stringWriter);
                try {
                    memcachedClient.set(id, EXPIRY_TIME, stringWriter.toString());
                } catch (IllegalStateException e) {
                    // ignore
                }
                env.getOut().write(stringWriter.toString());
            }
            else {
                body.render(env.getOut());
            }
        } else {
            throw new RuntimeException("Missing body.");
        }
    }
}
