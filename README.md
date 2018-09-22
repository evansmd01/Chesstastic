# Chesstastic!!!

Just a fun little chess game I made to experiment with Kotlin and because I've always wanted to build a chess AI. 

![Screenshot](https://i.gyazo.com/337df6aa1b8bb5d907d772852f6d7780.png)

#### Fast Checkmate: Wide AI (5 10) vs Deep AI (10 5)

![FastCheckmate](https://i.gyazo.com/67e4d6b95f03402b1d57c73701e4a868.png)

## Ideas / Notes / Internal Ramblings of a Madman

### Algorithms

- Minimax: For a given depth and breadth. Evaluate all possible player moves, select the best N(breadth). For each, evaluate all enemy responses, select the best 1. Until depth has been reached, recursively continue. Always end on an evaluation of the opponent response. Select the branch of moves that resulted in the best outcome. Play the first move in that chain. 

- Preanalysis: A single pass through of the board to gather metadata details that will be reused later for analysis and evaluation. Collecting numerous data points about every square on the board without yet judging the data. 

- Genetic Mutation: Store all numeric values used in any evaluation of the board in a config store. Start by randomly seeding values within reasonable ranges. Create many permuations. Pit them against each other. Select the top winners and use them as seeds for another round of modifications. Repeatedly select winners, mutate, compete, select winners, mutate, compete. 

### Datapoints to Collect per Square

Each squares value could be calculated by a formula involving a number of datapoints.

The value would be claimed when occupying the square.
Partial value would be claimed for controlling the square (i.e. attacking it to prevent enemy occupancy).

Consider using formulaic expressions with multiple variables and input from multiple data points to give genetic mutation more flexibiliy in how it will affect scores. 

Squares can be worth more/less depending on the player. A valuable square for Light may not be as valuable for Dark's position.  

- base value
    - conditional modifiers 
        - already castled
        - blocks enemy pawn promotion (n moves away)
- occupant value 
    - conditional modifiers
        - pawn has open line to promote (not blocked by enemy pawns. Other pieces are okay because they're essentially pinned)
        - king or rook still has option to castle
        - prevents enemy castling (by attacking pass through squares)
        - blocks friendly castling (negative points)
- attacking lines
    - up to three moves out:
        - for R,B,Q: This is all pieces in a straight line, including pieces behind other pieces
        - for a Knight, this is 3 hops in any direction. 
    - also used to detect forking. if at any point you can attack in two directions. 
- attacking enemies
    - number and value of enemies attacking this square. Can later be compared against supporting allies to determine value of exchange. 
- pinned to allies
    - any allies of greater value a piece is blocking from being attacked
- supporting allies
    - allies on squares which, if captured, could be recaptured
    - must be counted as 0 if pinned
- moves available
    - a measure of mobility. A rook on an open rank has more moves than a rook behind a pawn.
    - must be counted as 0 if pinned
- tempo
    - an occupant is on a sqaure that:
        - attacks a piece of greater value
        - is not under attack
            - or is supported by a square (or squares) of equal or lesser value than the enemy attacker(s). 

- 
