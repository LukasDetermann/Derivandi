# Derivandi

**Source code analysis and generation at compile time**
based on Annotation Processing, also called Annotation Processing Tool (APT).

Depending on your background, there are different guides:

- [new to metaprogramming](https://www.derivandi.com/Derivandi/Intro%20for%20new.html)
- [proficient in reflection or another metaprogramming technic](https://www.derivandi.com/Derivandi/Intro%20with%20meta.html)
- [proficient in annotation processing](https://www.derivandi.com/Derivandi/Intro%20with%20apt.html)


## When to use APT?

- The feedback cycle is as fast as possible. If generated sources are invalid the compiler will tell you
- It’s harder to make mistakes, as the compiler validates everything
- Meta Programming is always complex irrespective of the technology or api. Limiting it to compile time:
  - Enforces separation of concerns with a hard boundary for architecture erosion
  - Reasoning about an Annotation Processors impact on runtime is limited to the generated files and can therefore be drastically simpler
- No lock-in - generated sources can be added to the source tree and the generator removed

## When not to use APT?

* Annotation Processors are limited to compile time, if you need Meta Programming at runtime and generating behavioral patterns like Strategy is not enough
* Knowledge about the reflection is very common. Adding a new Technology increases the maintenance burden at least temporally

## Why this Library?

- Most projects build tooling around APT anyways. When migrating to this Library LOC for the Processor can be reduced by 20-30%
- This project is easier to use and avoids common pitfalls of APT
- It provides adapters so you’re not locked in and can easily work with other APT tooling

### Some key features are

- dependency free
- nulls save
- One Data Structure instead of two partially overlapping ones
- Types for Leafs of the abstract syntax tree, like Class, Enum or Method
- Java Source File DSL for easy generation
- Java Beans Support
- Generation of typesafe Annotation Representations

## Examples

- [Builder](https://www.derivandi.com/Derivandi/Builder%20Example.html)
- [Static Interface](https://www.derivandi.com/Derivandi/Static%20Interface.html)
- [Proxy](https://www.derivandi.com/Derivandi/Proxy%20Example.html)

## Next Goals

* Add DSL support for method body contents
* More examples