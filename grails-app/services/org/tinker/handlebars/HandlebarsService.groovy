package org.tinker.handlebars

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import javax.annotation.PostConstruct
import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import com.github.jknack.handlebars.context.FieldValueResolver
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import grails.util.GrailsUtil
import java.util.concurrent.ConcurrentHashMap

/**
 * Compile Handlebars template resources located under web-app. Compiled templates are cached.
 */
class HandlebarsService {

    def grailsApplication

    String templatesPathSeparator
    String templatesRoot

    Handlebars handlebars

    private Map<String, Template> templateCache

    @PostConstruct
    private void init() {
        def cfg = grailsApplication.config.grails.resources.mappers.handlebars
        templatesPathSeparator = cfg?.templatesPathSeparator ?: '/'
        templatesRoot = cfg?.templatesRoot ?: ''

        def cacheTemplates = grailsApplication.config.handlebars.cache.templates
        if (!(cacheTemplates instanceof Boolean)) cacheTemplates = !GrailsUtil.isDevelopmentEnv()
        if (cacheTemplates) templateCache = new ConcurrentHashMap<String, Template>()

        handlebars = new Handlebars()
    }

    /**
     * Apply a template provided as a resource to a model (Map, Java bean etc.) and return the generated String.
     */
    String apply(String templateName, Object model) {
        return compile(toResourceName(templateName)).apply(createContext(model))
    }

    /**
     * Apply a template provided as a String to a model (Map, Java bean etc.) and return the generated String.
     */
    String applyInline(String inlineTemplate, Object model) {
        return compileInline(inlineTemplate).apply(createContext(model))
    }

    /**
     * Converts a template name (e.g. 'profile/show') to a resource name under web-app (e.g.
     * 'templates/profile/show.handlebars') using settings from the handlebars-resources plugin.
     */
    String toResourceName(String templateName) {
        if (templatesPathSeparator != '/') templateName = templateName.replaceAll(templatesPathSeparator, '/')
        if (templatesRoot) templateName = templatesRoot + "/" + templateName
        return "/" + templateName + ".handlebars"
    }

    /** Compile a template provided as a resource. */
    Template compile(String resourceName) {
        return compileImpl(resourceName, true)
    }

    /** Compile a template provided as a String. */
    Template compileInline(String inlineTemplate) {
        return compileImpl(inlineTemplate, false)
    }

    private Template compileImpl(String t, boolean isResource) {
        if (templateCache) {
            def template = templateCache.get(t)
            if (template) return template
        }

        String text
        if (isResource) {
            def ins = ServletContextHolder.servletContext.getResourceAsStream(t)
            if (!ins) throw new FileNotFoundException("Resource not found: " + t)
            text = ins.getText("UTF8")
        } else {
            text = t
        }

        def template = handlebars.compile(text)
        if (templateCache != null) templateCache.put(t, template)
        return template
    }

    /** Create a context from a Map or Java bean etc. */
    Context createContext(Object model) {
        return Context.newBuilder(model)
                .resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE)
                .build()
    }
}
