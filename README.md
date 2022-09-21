# MazeGame
1. use command prompt, go to the \src directory
2. compile: `javac -d classes Tracker.java Game.java`
3. go to \classes directory: `cd classes`
4. start registry: `start rmiregistry`
5. run Tracker:  `java Tracker 1099 15 10`
6. open another commend prompt, run Game: `java Game <TrackerIP> 1099 <PlayerID>` - this is assinged as server
7. open another commend prompt, run Game: `java Game <TrackerIP> 1099 <PlayerID2>` - can try to ping server by inputting 5