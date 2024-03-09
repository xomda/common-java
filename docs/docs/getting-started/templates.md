---
outline: deep
---

# Templates

The templates can be used to generate assets — such as code — out of your Object Model.
Examples can be java interfaces and classes, but also configuration files (like Liquibase changesets) or even
documentation.

Below is a simple implementation of a template:

**_ExampleTemplate.java_**

```java
import org.xomda.template.Template;
import org.xomda.template.TemplateContext;

public class TestTemplate implements Template<Object> {

    @Override
    public void generate(Object o, TemplateContext ctx) {
        System.out.println("It works!");
    }

}
```

The templates should implement the `org.xomda.template.Template` interface, provided by XOMDA.

## Java Code Template

There are helpers to help you generate Java classes. The example below generates a Java interface; it's
borrowed straight from the XOMDA codebase.

First off all, we start by creating a new `JavaClassWriter`. This special context will assist you with
indenting, tabbing, writing Javadoc and more.

### Example template

The following Template will generate java beans, interfaces and enums.

**_GenerateEntityTemplate.java_**

```java
public class GenerateEntityTemplate extends PackageTemplate {

    @Override
    public void generate(final Entity entity, final TemplateContext context) throws IOException {
        String javaInterface = TemplateUtils.getJavaInterfaceName(entity);
        String javaClass = TemplateUtils.getJavaBeanName(entity);
        PojoWriter
                .createInterface(context.cwd(), javaInterface)
                .write(entity);
        PojoWriter
                .create(context.cwd(), javaClass)
                .withImplements(javaInterface)
                .write(entity);
    }

    @Override
    public void generate(final org.xomda.model.Enum enm, final TemplateContext context) throws IOException {
        EnumWriter.create(context.cwd(), TemplateUtils.getJavaEnumName(enm))
                .write(enm);
    }

}
```

### JavaImportService

Inside a `JavaClassWriter`-instance, there's an instance of a `JavaImportService`. This is a special class
which will assist you with managing class-level imports.

When writing the output, the `JavaClassWriter` will take care of declaring the package and printing the imports.
During the generation you can add imports to the `JavaImportService` and it will return the either the fully qualified
class name or the simple one, depending on the circumstances.

You can use `addImport`, `addStaticImport` and `addGenericImport` to add imports to the service.
