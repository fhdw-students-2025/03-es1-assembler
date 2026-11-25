# assembler

Assembles a source file into machine code. Built for the ES1 lecture at FHDW Hannover.

## Usage

```
assembler <source> <output>
```

Download `assembler.exe` from the [releases](../../releases), or build it yourself (Java 25):

```
mvn package
java -jar target/assembler.jar <source> <output>
```

Run `assembler --help` for all options.
