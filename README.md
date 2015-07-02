# Battleship-Extreme
This is a project for the semester "Programming 2" at the ["Hochschule Bremen"](http://www.hs-bremen.de/).

Contributors are:
* [mueller-jan](https://github.com/mueller-jan/)
* [lschoenawa](https://github.com/lschoenawa/)
* [cschaf](https://github.com/cschaf/)

## What is Battleship?
Battleship (or Battleships) is a game for two players where you try to guess the location of five ships your opponent has hidden on a grid. Players take turns calling out a row and column, attempting to name a square containing enemy ships. Originally published as Broadsides by Milton Bradley in 1931, the game was eventually reprinted as Battleship.
## Goal
The object of Battleship is to try and sink all of the other player's before they sink all of your ships. All of the other player's ships are somewhere on his/her board.  You try and hit them by calling out the coordinates of one of the squares on the board.  The other player also tries to hit your ships by calling out coordinates.  Neither you nor the other player can see the other's board so you must try to guess where they are.  Each board in the physical game has two grids:  the lower (horizontal) section for the player's ships and the upper part (vertical during play) for recording the player's guesses.
## What is so extreme in our game?
We added a few rules which will make the game more interesting and more difficult.
## Known Rules
* Each player places the 5 ships somewhere on their board. The ships can only be placed vertically or horizontally. Diagonal placement is not allowed. No part of a ship may hang off the edge of the board.  Ships may not overlap each other.  No ships may be placed on another ship. 
* Once the guessing begins, the players may not move the ships.
* The 5 ships are:  Carrier (occupies 5 spaces), Battleship (4), Cruiser (3), Submarine (3), and Destroyer (2).
* Player's take turns guessing by calling out the coordinates. The opponent responds with "hit" or "miss" as appropriate.  Both players should mark their board with pegs:  red for hit, white for miss. For example, if you call out F6 and your opponent does not have any ship located at F6, your opponent would respond with "miss".  You record the miss F6 by placing a white peg on the lower part of your board at F6.  Your opponent records the miss by placing.
* When all of the squares that one your ships occupies have been hit, the ship will be sunk.   You should announce "hit and sunk".  In the physical game, a red peg is placed on the top edge of the vertical board to indicate a sunk ship. 
* As soon as all of one player's ships have been sunk, the game ends.

## Our added or updated Rules
* Determining how many players play. 2-6
* Definition of the square field.
* Determining how many destroyers, frigates, corvettes and submarines be used (no cruiser allowed)
* Players place their ships in succession. Destroyers take 5, frigates 4, corvettes 3 and submarines 2 Fields. The Ships may only be placed horizontally or vertically in the field, overlaps
are not allowed, also the ships must not abut each other there must always be at least
be an empty field between two ships.
* Players can shoot one after the other. There are the following sequence

  1.  Choose one of the available ship.
  2.  Selection of an opponent.
  3.  Select the coordinate on the pitch.
  4.  The opponent says that the shot went into the water hits a ship, or if a ship was sunk.

* A destroyer shoot with big guns. It will be taken 3 boxes next to each other. though
the destroyer must always download 3 laps and is not available in time. a frigate
2 hits fields side by side. She has a recharge time of 2 rounds. A Corvette hits 1 field. She has a recharge time of 1 round. A submarine hits 1 field and also has a recharge time of 1 round. It can of course only non-sunken ships shoot.
* When all the ships of a player were sunk, he is out. The game ends when only one player remains.


## Project-Structure
### Packages
![UML Class Diagram 1](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/uml_diagram_packages.png)
### Models
![UML Class Diagram 2](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/uml_diagram_model.png)
### Client
![UML Class Diagram 3](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/uml_diagram_client.png)
### Server
![UML Class Diagram 4](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/uml_diagram_server.png)
### Network
![UML Class Diagram 5](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/uml_diagram_network.png)

## Activity-Diagrams
### Sending an object to the server
![Sequenz-Diagram 1](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/sequencediagram_send_object_over_network.png)

### A Multiplayer Game
![Activity-Diagram 1](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/activity_diagram_game.png)


