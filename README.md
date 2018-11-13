# Maven Template
Requirements
    Java 1.8
    Maven

How to use
    Open a terminal and cd into the project's root.
    Create .jar by running:

$ mvn clean install

This should have generated a .jar file under ./target/ folder.

    Execute .jar by running:

$ java -jar target/Reversi-1.jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off] -load [file]

Where:
● -size [n]: determines the size of the board. "n" must be an unsigned integer.
● -ai [m]: determines the role of the AI. "m" is a number that means:
○ 0: there is no AI. Two human players
○ 1: AI moves first
○ 2: AI moves second

● -mode [time | depth]: determines if the minimax algorithm runs by time or bydepth
● -param [k]: accompanies the previous parameter. In the case of "time", k must be the seconds. 
In the case of "depth", it must be the depth of the tree.
● -prune [on | off]: activates or deactivates pruning.
● -load [file]: optional. This game must load the previously saved game.(In this case it does not need to 
be accompanied by the size parameter, and if it is, size must be the same as in the saved game)
