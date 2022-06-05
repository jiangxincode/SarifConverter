# SarifConverter

Contains POJOs generated from the [Static Analysis Results Interchange Format 
](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html) (SARIF) 
[JSON schema](https://github.com/oasis-tcs/sarif-spec/blob/master/Schemata/sarif-schema-2.1.0.json).

It uses Jackson for serialising/deserialing from JSON.

## Usage

### Add as a dependency

```xml
<dependency>
  <groupId>edu.jiangxin.sarif.converter</groupId>
  <artifactId>SarifConverter</artifactId>
  <version>${VERSION}</version>
</dependency>
```

#### Building Objects

Building is provided with method chaining, e.g. for Message

```java
import edu.jiangxin.sarif.converter.Message;
// ...
Message message = new Message()
    .withText("SQL Injection")
    .withMarkdown("# SQL Injection");
```

Public Getters & Setters are provided.

#### Jackson

Classes are decorated with `@JsonInclude(JsonInclude.Include.NON_DEFAULT)` and `@JsonPropertyOrder`
which dictates the order from the JSON schema.

```java
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
// ...
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({
    "text",
    "markdown",
    "id",
    "arguments",
    "properties"
})
public class Message {
    // ...
}
```
