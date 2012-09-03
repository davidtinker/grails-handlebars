# Handlebars Grails Plugin

This plugin provides for server side execution of [Handlebars.js](http://handlebarsjs.com/) templates. It is
intended to compliment the [handlebars-resources](https://github.com/sheehan/grails-handlebars-resources) plugin
which compiles Handlebars templates into Javascript for client side usage.

## Installation

    grails install-plugin handlebars

Note that you need to install the handlebars-resources plugin separately if you also want to use handlebars.js on
the client:

    grails install-plugin handlebars-resources

## Usage

A handlebars:render tag is provided to render handlebars templates server side in views and from controllers. The
template can be provided inline:

    <handlebars:render model="[name: 'bob']">
    <p>Hello {{name}}</p>
    </handlebars:render>

Or stored in a separate file and referenced by name:

    <handlebars:render template="templates/home/hello" model="[name: 'bob']"/>

Put the template code in ''web-app/templates/home/hello.handlebars''.

You probably want to put the following in your Config.groovy:

    grails.resources.mappers.handlebars.templatesRoot=templates

Then you can reference template names without the templates prefix:

    <handlebars:render template="home/hello" model="[name: 'bob']"/>

If no model is supplied then the default page bindings are used:

    <handlebars:render>
    Hello {{name}} from the controller
    </handlebars:render>

You can also use the HandleBars service used by the taglib:

    def handlebarsService
    ...
    String html = handlebarsService.apply("home/accounts", [accounts: Account.list()])

## Configuration

The following configuration variables from handlebars-resources are also used by this plugin:

* **grails.resources.mappers.handlebars.templatesRoot**: The root folder of the templates relative to 'web-app'. This
  value will be stripped from template paths when calculating the template name. Default is none.
* **grails.resources.mappers.handlebars.templatesPathSeparator**: The delimiter to use for template names.
  Default is '/'.

## Thanks

Thanks to Edgar Espina for creating the [handlebars-java](https://github.com/jknack/handlebars.java) library used
by this plugin and to Matt Sheehan for the [handlebars-resources](https://github.com/sheehan/grails-handlebars-resources)
plugin

## Changelog

1.0.0: 3rd Sept 2012 - Initial release
