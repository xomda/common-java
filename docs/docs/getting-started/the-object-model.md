---
outline: deep
---

<script setup>
    import CsvTable from "../../src/components/CsvTable.vue"
</script>

# The Object Model

The Object Model is specified in CSV-format, where each row represents a single object. An Object Model consists of 2
parts:

- The schema of the object model
- The model definitions

XOMDA comes with a default object model, as shown below:

<img src="/object-model-diagram.svg" width="300" style="margin:2em auto;" />

It's a simple data structure which provides the building blocks for defining a complex object model.
When using the default XOMDA object model in your own project, it will be parsed and represented in this structure.

## The model schema

The first rows of the CSV specify the schema. The first blank line distinguishes the schema from the actual model.

Each line in the CSV schema specifies a type of Object. The **name** of this Object is defined in the first column. Its
**properties** are defined in the subsequent columns, in the same column index as they will be encountered in the model
definition following the schema.

<CsvTable data="
Package;name;packageName;;;;;;;;;description
Enum;name;identifier;;;;;;;;;description
Value;name;;;;;;;;;;description
Entity;name;identifier;parent;type;;dependency;transient;;;;description
Attribute;name;identifier;type;size;scale;enumRef;entityRef;dependency;multiValued;required;description
"/>

It's also possible to "hard-code" the package, so that you have complete control over the interface that matches the
schema object. This can be achieved by specifying a fully qualified class name in the object model's schema.

The following example is the exact same schema as the one above, only with fully qualified interfaces:

<CsvTable data="
org.xomda.model.Package;name;packageName;;;;;;;;;description
org.xomda.model.Enum;name;identifier;;;;;;;;;description
org.xomda.model.Value;name;;;;;;;;;;description
org.xomda.model.Entity;name;identifier;parent;type;;dependency;transient;;;;description
org.xomda.model.Attribute;name;identifier;type;size;scale;enumRef;entityRef;dependency;multiValued;required;description
"/>

### How the schema is parsed

First, the CSV parser will try to match a corresponding Java interface, which matches the name of the current object.
The name of object the is defined in the first column; for the default embedded XOMDA model, this would then be
_Package_, _Enum_, _Entity_ and _Attribute_.  
The packagename of the object is can be specified in the settings, and defaults standard to `org.xomda.model`.
You can specify multiple package names where to search for the matching interfaces.

Another way of specifying the package, is to just provide a fully qualified classname for the object, as illustrated above.

Next, when a corresponding Java interface is found, the parser will try to figure out the type of each property that is
defined in the schema. This is done by looking for getters with a corresponding name and look at their return type.

### Linking models together

An Attribute should know it's attached to an Entity and a Value should know it's attached to an Enum.
XOMDA comes with a plugin (XOMDAReverseEntity) which will enable this behavior.  
The plugin will look for a specific collection on the parent and a back-reference to the parent on the child.

For example: The Java interface for Entity has a getter called `Collection<Attribute> getAttributeList()`,
while Attribute has a getter called `Entity getEntity()`.
This ties the both together, such that the parser knows that it should add Attributes to the previously defined Entity.

So when defining your own model, keep this principle in mind if you want to create parent / child relationships.

## The model definition

The subsequent lines following the schema, are the object model definition itself. Each line will start with one of the
Objects defined in the Schema.

For example, defining a Package is done like so:

<CsvTable data="
Package;XOMDA;org.xomda;;;;;;;;;
"/>

... or an Enum like so:

<CsvTable data="
Enum;Weekday;wkd;;;;;;;;;
Value;Monday;1;;;;;;;;;
Value;Tuesday;2;;;;;;;;;
Value;Wednesday;3;;;;;;;;;
Value;Thursday;4;;;;;;;;;
Value;Friday;5;;;;;;;;;
Value;Saturday;6;;;;;;;;;
Value;Sunday;7;;;;;;;;;
"/>

... or an Entity like so:

<CsvTable data="
Entity;Customer;cst;;Dynamic;;Aggregate;;;;;
Attribute;First Name;fname;String;255;;;;;;;
Attribute;Last Name;lname;String;255;;;;;;;
Attribute;E-mail;eml;String;255;;;;;;;
"/>

## Extending the object model

If you want to create a custom object model, you can do so by creating new Java interfaces for each Object found in the
CSV schema.  
As the parser creates proxy objects for these interfaces, it's not needed to provide implementation classes. The proxy
objects will function as instances of the given interface, without the need of an actual class.

It's both possible to extend the existing schema objects — provided by XOMDA — or create complete new objects which have
nothing to do with the default XOMDA object model.

### Adding new schema objects

The following line in a CSV schema will introduce an object called `Fish`, which has a name and an age:

<CsvTable data="
Fish;name;age;
"/>

In order to make the parser understand `Fish`, it needs to find the Fish Java interface at the given classpath in the
configuration.  
The Java interface may then look something like this:

```java
package your.package.name;

public interface Fish {

    String getName();

    int getAge();

}
```

The compiler can then use this interface to correctly determine the Java types being used (because CSV only contains
String values).

### Extending existing Schema objects

You may want to just extend the default functionality of the default Object Model. For example, you may just want to add
an extra attribute, or some new Reverse Entity relationship.

```java
package your.package.name;

import org.xomda.model.Entity

public interface MyEntity extends Entity {

    Long getSomethingExtra();

    setSomethingExtra(Long somethingExtra);

}
```

By doing so, you're now able to use `MyEntity` instead of `Entity` provided by XOMDA.
