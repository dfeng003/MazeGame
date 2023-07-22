# MazeGame
1. use command prompt, go to the \src directory
2. compile: `javac -d classes Tracker.java Game.java`
3. go to \classes directory: `cd classes`
4. start registry: `start rmiregistry`(for windows systerm), `rmiregistry &` (for mac)
5. run Tracker:  `java Tracker 1099 15 10`(for mac, you need to go to the \src\classes directory first)
6. open another commend prompt, run Game: `java Game <TrackerIP> 1099 <PlayerID>` - this is assigned as server
(for mac, you need to go to the \src\classes directory first)
7. open another commend prompt, run Game: `java Game <TrackerIP> 1099 <PlayerID2>`- this is assigned as backup server
(for mac, you need to go to the \src\classes directory first)
8. To run the stressTest, instead of 6-7, run: `java StressTest.java <TrackerIP> 1099 "java Game"`
