# JShip

JShip is a Java based single-player implementation of the board game Battleship, using the Swing GUI framework.

It is also the investigatory project for 2 high school students (and hence the reason why Java and MySQL are used).

## Warning
 * This project is currently under active design and development.
 * The author takes no responsibility for any damages, to either software, hardware, yourself or others, caused directly or indirectly by the usage of this application.

## License
[GNU GPLv3 License](http://www.gnu.org/licenses/gpl.html "The GNU General Public License v3.0 - GNU Project - Free Software Foundation")

## Usage
1. Requires a Java Runtime Environment (has only been test on JRE version 8).
2. For statistics and user profiles:
	1. Requires a local MySQL server.
	2. Run ```src/misc/create.sql``` before running the application for the 1st time.
	3. You may have to modify the JDBC connection variables in the misc.DBDetails class depending on your SQL setup.
3. Compile and run jship.JShip (may distribute .jar files later).

## TODO
* Ships not being able to be placed beside each other (no contact between ships).
* AI Difficulties (other than a random number generator).
* Make it beautiful.
