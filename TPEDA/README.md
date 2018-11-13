How to use

1. Open a terminal and `cd` into the project's root.
2. Create `.jar` by running:

$ mvn clean install

This should have generated a `.jar` file under `./target/` folder.

4. Execute `.jar` by running:

$ java -jar .jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off] -load [file]


