# Chesstastic!!!

Just a fun little chess game I made to experiment with Kotlin and because I've always wanted to build a chess AI. 

![Screenshot](https://i.gyazo.com/337df6aa1b8bb5d907d772852f6d7780.png)

## Backlog

If you're curious, [check out the Pivotal Tracker board!](https://www.pivotaltracker.com/n/projects/2199679)

## Milestones:

**11 Sep 18** - Initial commit, repo setup

**15 Sep 18** - Begin building game logic

**16 Sep 18** - Complete human v human game logic

**19 Sep 18** - AI can achieve checkmate

**22 Sep 18** - AI plays it's first game against Stockfish (world's leading chess AI). Get's destroyed. 

## Where the dependencies at?

You may have noticed I'm not pulling in any external dependencies. Professionally this is a terrible idea. If a library already exists that can do what you need done, don't waste time re-inventing the wheel. Developer time is expensive and it's irresponsible to waste it. 

That said, if you want to really learn a new language (and you're not charging for your time), don't use libraries. Building your own frameworks and libraries is the best way I've found to learn all the little ins and outs of how to bend the syntax to your will. Plus it's more fun. Win - win. 

So for this project, I'm limiting myself to the standard library. Kotlin makes it easy anyway. Check out the testing framework! 

## Description of Algorithms

// TODO: add descriptions for minimax, positional evaluation, and genetic learning. 

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


