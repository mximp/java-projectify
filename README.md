# Java Projectify

CLI tool for assisting in Java project structure creation

# Usage

Suppose you have the following two files within current folder:

```java
// ClassA.java
package org.sctt.tools.a;

/**
 * ClassA.
 */
public final class FileA {
    
}
```

```java
// ClassB.java
package org.sctt.tools.a;

/**
 * ClassB
 */
public final class FileB {
    
}
```

Running command `jpfy .` would produce the following folder structure:
```
.
org\
|-- sctt\
    |-- tools\
        |-- a\
            |-- ClassA.java
        |-- b\
            |-- ClassB.java
```