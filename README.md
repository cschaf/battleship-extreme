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
### Number of Players
The game can be played by 2 to 6 players.

### Game Board Definition
The game is played on a defined square field.

### Ship Composition
Players determine the number of destroyers (5 fields), frigates (4 fields), corvettes (3 fields), and submarines (2 fields) to be used. Cruisers are not allowed in this version.

### Ship Placement Rules
Players place their ships in succession.
* Ships can only be placed vertically or horizontally.
* No part of a ship may hang off the edge of the board.
* Ships may not overlap each other.
* Ships must not abut each other; there must always be at least one empty field between two ships.

### Shooting Sequence
Players take turns shooting. The sequence for a shot is as follows:
  1.  Choose one of your available (non-sunken) ships to fire.
  2.  Select an opponent to target.
  3.  Select the coordinate(s) on the opponent's pitch.
  4.  The opponent announces whether the shot was a "miss" (into the water), a "hit" (on a ship), or if a ship was "sunk".

### Special Ship Abilities and Rules
*   **Destroyer**: Shoots with big guns, affecting 3 boxes next to each other. Requires a 3-lap recharge time (unavailable during recharge).
*   **Frigate**: Hits 2 fields side by side. Requires a 2-round recharge time.
*   **Corvette**: Hits 1 field. Requires a 1-round recharge time.
*   **Submarine**: Hits 1 field. Requires a 1-round recharge time.
*   Only non-sunken ships can shoot.

### Player Elimination and Game End
* When all the ships of a player are sunk, that player is eliminated from the game.
* The game ends when only one player remains.


## How to play
### Local
![Client GUI](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/how%20to%20play/local.png)
  1. In the menu screen you have to click on "Local Game".
  2. In the settings screen you have to set the game settings and start the game by click on "OK".
  3. Now you can play.

### Multiplayer
![Server GUI](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/how%20to%20play/server.PNG)
  1. In the server gui you have to start the server.
  2. On the top menu you can create a standard game by click on "Games" -> "Create standard game".
  3. In the client gui you have to click on "Multiplayer Game".
  4. Enter the ip-address and port of the server, choose a username and connect to the server by click on "Connect".
  5. Now you can join existing games or create a custom game. To join a game, select a game from the list and click "Join". To create your own game, click on "Create" and enter the game settings. After the game was created you can join it.
  5. The game will start when all players are connected.

### Gameplay
![test](https://raw.githubusercontent.com/cschaf/battleship-extreme/develop/documentation/how%20to%20play/game.PNG)

1. At the beginning of the game each player has to place his ships on the right board and confirm by click on "Done".
2. When all ships are placed the players can shoot on each other.
3. To take a shot you have to
	1. Select an enemy from the box called "Enemys".
	2. From the box called "Your Ships" select a ship which should take the shot.
	3. Change the orientation of your shot by click on the right mouse button or selecting it manually in the box called "Orientation".
	4. Choose a valid field on the left board and click on it.
4. To save a local game click on "Game" in the top menu and then click "Save...".

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


