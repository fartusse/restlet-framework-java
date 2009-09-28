/**
 * Copyright 2005-2008 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of the following open
 * source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 (the "Licenses"). You can
 * select the license that you prefer but you may not use this file except in
 * compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.sun.com/cddl/cddl.html
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royaltee free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package com.noelios.restlet.ext.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Restlet;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.FrameworkServlet;

import com.noelios.restlet.ext.servlet.ServletConverter;

/**
 * A Servlet which provides an automatic Restlet integration with an existing
 * {@link org.springframework.web.context.WebApplicationContext}. The usage is
 * similar to Spring's {@link org.springframework.web.servlet.DispatcherServlet}
 * . In the web.xml file, declare the Servlet and map its root URL like this:
 * 
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;api&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.noelios.restlet.ext.spring.RestletFrameworkServlet&lt;/servlet-class&gt;
 *    &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;api&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/api/v1/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * <p>
 * Then, create a beans XML file called
 * <code>/WEB-INF/[servlet-name]-servlet.xml</code> &mdash; in this case,
 * <code>/WEB-INF/api-servlet.xml</code> &mdash; and define your restlets and
 * resources in it.
 * <p>
 * All requests to this servlet will be delegated to a single top-level restlet
 * loaded from the Spring application context. By default, this servlet looks
 * for a bean named "root". You can override that by passing in the
 * <code>targetRestletBeanName</code> parameter. For example:
 * 
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;api&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.noelios.restlet.ext.spring.RestletFrameworkServlet&lt;/servlet-class&gt;
 *    &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;targetRestletBeanName&lt;/param-name&gt;
 *       &lt;param-value&gt;guard&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * </pre>
 * 
 * @author Rhett Sutphin
 */
public class RestletFrameworkServlet extends FrameworkServlet {
    /** The default bean name for the target Restlet. */
    private static final String DEFAULT_TARGET_RESTLET_BEAN_NAME = "root";

    private static final long serialVersionUID = 1L;

    /** The converter of Servlet calls into Restlet equivalents. */
    private volatile ServletConverter converter;

    /** The bean name of the target Restlet. */
    private volatile String targetRestletBeanName;

    @Override
    protected void doService(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        this.converter.service(request, response);
    }

    /**
     * Returns the target Restlet from Spring's Web application context.
     * 
     * @return The target Restlet.
     */
    protected Restlet getTargetRestlet() {
        return (Restlet) getWebApplicationContext().getBean(
                getTargetRestletBeanName());
    }

    /**
     * Returns the bean name of the target Restlet. Returns "root" by default.
     * 
     * @return The bean name.
     */
    public String getTargetRestletBeanName() {
        return (this.targetRestletBeanName == null) ? DEFAULT_TARGET_RESTLET_BEAN_NAME
                : this.targetRestletBeanName;
    }

    @Override
    protected void initFrameworkServlet() throws ServletException,
            BeansException {
        super.initFrameworkServlet();
        this.converter = new ServletConverter(getServletContext());
        this.converter.setTarget(getTargetRestlet());
    }

    /**
     * Sets the bean name of the target Restlet.
     * 
     * @param targetRestletBeanName
     *            The bean name.
     */
    public void setTargetRestletBeanName(String targetRestletBeanName) {
        this.targetRestletBeanName = targetRestletBeanName;
    }
}