# Chesstastic!!!

Just a fun little chess game I made to experiment with Kotlin and because I've always wanted to build a chess AI. 

![Screenshot](https://i.gyazo.com/e82175d99b7c47005e01dc5316159c59.png)

![Checkmate](https://i.gyazo.com/c3d43c29ea5428d9e32afd7bcb8b4dce.png)

## Backlog

- The next steps are to add more heuristics and tweak them to a reasonable starting balance. 

- Then I'll be adding a genetic learning algorithm to essentially spawn many generations of slightly tweaked weighting criteria, using pre-evaluated positional scores from Stockfish initially.

- Then, once it's a fairly strong player, I'll start playing it against stockfish for large sets of games. Using the same genetic algorithm to find small improvements. 

If you're curious, [check out the Pivotal Tracker board!](https://www.pivotaltracker.com/n/projects/2199679)

*P.S. Pivotal Rulez, JIRA Droolz. Come at me, bro!*

## Milestones:

**11 Sep 18** - Initial commit, repo setup

**15 Sep 18** - Begin building game logic

**16 Sep 18** - Complete human v human game logic

**19 Sep 18** - AI can achieve checkmate

**22 Sep 18** - AI plays it's first game against Stockfish (world's leading chess AI). Get's destroyed. 

**28 Sep 18** - Optimized positional metadata processing to evaluate ~10k positions / second

**28 Sep 18** - First working heuristics get created. AI starts fighting for control of the center. Still rudimentary, but much more exciting to watch.

## Testing Framework

Kotlin is so cool. 

I wrote my own testing framework because I figured it would help learn Kotlin to roll my own instead of pulling one in, and it seemed like a good use of the builder syntax Kotlin is so proud of. 

The whole thing is only ~200 lines of code, and includes: 
- nested description blocks, 
- skipping tests, 
- running individual tests, 
- tracking time and results, 
- and a few fluent assertion helpers.

Check it out:

![Code](https://i.gyazo.com/894ede9b464bece6eb2a13ba2c24c40f.png)

![Output](https://i.gyazo.com/e77fde4ab773b723dbb92e911ae1aaa3.png)


## Installing Stockfish

This project uses Stockfish (the world's leading chess AI), as an opponent for the Chesstastic AI, and for generating training data for the genetic learning algorithm. There are some unit tests for the proxy to the external Stockfish process, so you need to compile Stockfish before tests will pass. 

```
# from terminal, from the root of the git repo
cd lib/Stockfish
git submodule update --init
cd src
make build ARCH=x86-64
make strip
```

Based on OSX [install instructions](http://support.stockfishchess.org/kb/advanced-topics/compiling-stockfish-on-mac-os-x)


