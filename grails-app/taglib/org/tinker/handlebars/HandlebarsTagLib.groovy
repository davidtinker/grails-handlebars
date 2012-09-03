package org.tinker.handlebars

/**
 * Tag to render a model using Handlebars templates.
 */
class HandlebarsTagLib {

    static namespace = "handlebars"

    def handlebarsService

    /**
     * <p>Renders a Handlebars template resource with a model:</p>
     * <pre>
     * &lt;handlebars:render template="profile/show" model="[user:user,company:company]" /&gt;
     * </pre>
     *
     * <p>The template can also be inline:</p>
     * <pre>
     * &lt;handlebars:render model="[name: 'bob']"&gt;
     * Hello {{name}}
     * &lt;/handlebars:render&gt;
     * </pre>
     *
     * <p>If the model is not specified then the bindings for the page are used:</p>
     * <pre>
     * &lt;handlebars:render&gt;
     * Hello {{name}} from the controller
     * &lt;/handlebars:render&gt;
     * </pre>
     *
     * @attr template The name of the template resource to apply
     * @attr model The model to apply the template against (java.util.Map or Java bean)
     */
    Closure render = { attrs, body ->
        def model = attrs.model ?: getPageScope().variables
        if (attrs.template) out << handlebarsService.apply(attrs.template, model)
        else out << handlebarsService.applyInline(body(), model)
    }

}
