# Tetris
An online multiplayer implementation of the classis Tetris. Made for my AP Computer Science Class.

## Gameplay
![Gameplay Screenshot](https://i.imgur.com/Udo71cy.png)
The game is simple and familiar; it's classical Tetris except cleared lines are sent over to other players, making their odds of losing greater.
Rather than accumulating points as in normal Tetris, the goal is to survive longer than the other players.

A quick tour:
* Blocks on the left
  * The queue of next blocks; the top most is the nearest coming block, use this information strategically!
* Large Tetris Board on the left
  * Your Tetris Board which you can control with the arrow keys(all four are used)
* Small Tetris Board(s) on the right
  * Opponent's boards. Greyed out ones indicate that player has lost.

## Launch Instructions
1. Download the [game launcher](https://drive.google.com/file/d/1IqL84UqsugBhCcX6sG0QZCT0pi_m4zk_/view?usp=sharing).
1. Open it by double-clicking(Windows) or right-click --> "Open" on Mac. It's important to right-click-open on Mac!!!
1. A launcher window will prompt you to wait(for the game itself to download) then ask for your username.
1. Enter the field and hit play!

## Server Launch Instructions
1. Download the [server build](https://drive.google.com/file/d/1tcFedEeP6zy6TAttyz_8US-h_A9OlH2Q/view?usp=sharing).
1. Configure the IP Address in the [master server configuration sheet](https://docs.google.com/spreadsheets/d/1lC6AnZgw4LGute_icCiwuRZVOLTbTUJC7ckkOgK6MXU/edit?usp=sharing).
  * Discover your IP address using services such as [https://whatsmyip.com/](whatsmyip.com). "localhost" or "127.0.0.1" also works when running locally. I recommend running on LAN with friends unless you're familiar with NAT punchthrough(The game uses TCP and UDP.
  * If you're denied access from this document, it's most likely because you're a stranger reading this :P
  * Instead of using the launcher, run the [latest client build](https://drive.google.com/file/d/1dJVABfomO4DkOfosGu4L_yK3B_s-UZmm/view?usp=sharing) directly with arguments:
  > java -jar LATEST_CLIENT_BUILD_PATH serverIP tcpPort udpPort username
1. Configure the maximum number of players in that same configuration document. The server automatically starts the game once that number is reached
1. Double click the downloaded server build and connect clients!

## Dependencies & Credits
* [Java Development Kit](https://jdk.java.net/)
* [Kryonet](https://github.com/EsotericSoftware/kryonet)
* Everything else made by me.
