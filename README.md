# Chess engine
A basic chess engine written in Java. 

It also includes a basic min-max algorithm with alpha-beta pruning and quiescence search.

### How to play:
 - Run ChessCLI.java to run command line interface
 ```java -cp src Game.ChessCLI```
 - Run ChessGUI.java to run GUI
 ```java -cp src Game.ChessGUI```
 - In- and outputs are text-based
 - Whether white and black are player-controlled can be inputted with "True" or "False". The latter results in white or black being controlled by the min-max algorithm.
 - Computer difficulty can be set with integers (difficulty refers to default depth in min-max algorithm)
 - Checkmate is not implemented, so a match has to end by taking the opponents king.
 - Matches can be saved as .txt files after ending.