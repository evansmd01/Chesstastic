# Chesstastic!!!

Just a fun little chess game I made to experiment with Kotlin and because I've always wanted to build a chess AI. 

![Screenshot](https://i.gyazo.com/337df6aa1b8bb5d907d772852f6d7780.png)

## Milestones:

**11 Sep 18** - Initial commit, repo setup

**15 Sep 18** - Begin building game logic

**16 Sep 18** - Complete human v human game logic

**19 Sep 18** - AI can achieve checkmate

**22 Sep 18** - AI plays it's first game against Stockfish (world's leading chess AI). Get's destroyed. 

## Where the dependencies at?

You may have noticed I'm not pulling in any external dependencies. Professionally this is a terrible idea. If a library already exists that can do what you need done, don't waste time re-inventing the wheel. Developer time is expensive and it's irresponsible to waste it. 

That said, if you want to really learn a new language... Don't use libraries. Building frameworks and libraries is the best way I've found to learn all the little ins and outs of how to bend the syntax to your will. 

So for this project, I'm limiting myself to the standard library. Kotlin makes it easy anyway. 

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


## Installing Stockfish

This project uses Stockfish as an opponent for training & testing the AI. Stockfish is the top ranked chess AI at this time. 

```
cd lib/Stockfish
git submodule update --init
cd src
make build ARCH=x86-64
make strip
```

Based on OSX install instructions: http://support.stockfishchess.org/kb/advanced-topics/compiling-stockfish-on-mac-os-x


