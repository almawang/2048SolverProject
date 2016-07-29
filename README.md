# 2048 Solver: Final Project for CS 76 Artificial Intelligence
As a final project for my CS 76 course I developed an implementation for a 2048 Solver with a graphical display. Additional information and analysis of my project's performance can be found in the pdf. This README file contains an overview of the files.

## Overview of packages
package com.bulenkov.game2048 - Java implementation of 2048 by bulenkov. Used as basis for game implementation and display of project
	
package solver2048 - Contains code written for project

## Overview of Game Files
Game2048.java - models the 2048 game
	* Input: either Game2048() or Game2048(int target) where target is the target score
	
Game2048View.java - provides GUI display for the game

Game2048Driver.java - used to run the game and AI moves
* Comment/uncomment desired player in the main method, around line 72
* Can also change the depth searched at a significant time costs
* Test higher target scores in lines 62-64
* benchmark(int n) performs n runs of a particular player and game setting and prints out data
	- Comment/uncomment player or type of game to change

## Overview of AI Players Files

RandomPlayer.java - calculates the average utility of a move by performing a sequence of random moves for a set number of runs
*Input: RandomPlayer(int r) where r = number of runs to average

ExpectedMaxPlayer.java - calculates the expected maximum utility of each move to.
Heuristics provided to set to true/false in the beginning of the file:
* BLANKSPACES - prefers state with more blank spaces
* EDGES - increases utility of states where higher valued tiles are on the edges
* MAXCORNER - increases utility of state where highest valued tile is in the corner
* MONOTONIC - decreases utility of state where edges are not monotonic (either increasing or decreasing)
	
Additional features:
*PIECEUTILTIY - calculate expected maximum probability based on tile placement. Otherwise random tile placement and result not successful.
* PROBCONSTANT - sets minimum limit for a sequence of tile values to be considered probable. Used to prune tiles explored

AlphaBetaPlayer.java - uses alpha beta pruning to determine move where adversary's moves are different tile placements
Same heuristics as ExpectedMaxPlayer.java
Additional features:
* PROBCONSTANT as described above
* NOFOURS - assumes all new tiles will have the value 2
