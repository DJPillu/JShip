# coding=utf-8
"""
Program Name: Battleship
Author: blackk100
License: MIT License
Description: A Python3 based, CLI implementation of the board game, Battleship.
Current Version: Alpha
"""

# Imports

import os
from time import sleep
from random import randint as rand

# Generic Functions


def cls():
	"""Clears the terminal screen"""
	sleep(0.1)
	os.system("cls" if os.name == "nt" else "clear")


def fancy_print(li, flag_):
	"""Conditional aesthetically pleasing printing"""
	if flag_ is True:
		for _ in li:  # Line-by-line with time gap
			sleep(0.09)
			print(_, end="", flush=True)
	else:  # No time gap
		for _ in li:
			print(_, end="", flush=True)


def title_art(flag_):
	"""Prints the titular battleship"""
	t = [
		"\n                                           |__",
		"\n                                           |\\/",
		"\n                                           ---",
		"\n                                          / | [",
		"\n                                   !      | |||",
		"\n                                 _/|     _/|-++'",
		"\n                             +  +--|    |--|--|_ |-",
		"\n                          { /|__|  |/\\__|  |--- |||__/",
		"\n                        +---------------___[}-_===_._____'",
		"\n                    ____`-' ||___-{]_| _[}-  |     |_[___\\==--            \\/    _",
		"\n      __..._____--==/___]_|__|_____________________________[___\\==--____,------'' .7",
		"\n     |                                                                             /",
		"\n     |                                BATTLESHIP                                  /",
		"\n     |                                   Alpha                                   |",
		"\n      \\_________________________________________________________________________|\n"]
	fancy_print(t, flag_)


def menu(flag_):
	"""Prints the menu"""
	menu_ = [
		"\n Press CTRL + C anytime to force exit if the game gets stuck.",
		"\n 1. Play",
		"\n 2. Credits",
		"\n 3. Exit"]
	fancy_print(menu_, flag_)


def menu_nav(flag1, flag2):
	"""Menu navigation"""
	cls()
	title_art(flag1)
	menu(flag1)
	fancy_print("\n Option:", flag1)
	if flag2 == 1:
		fancy_print("\n Please enter a valid number! (1 <= option <= 3)\n", True)
	return input(" ")


def credits_(flag_):
	"""Prints the credits"""
	credit = [
		"\n\n Pre-Alpha Version",
		"\n\n Programming & Design - blackk100 - https://github.com/blackk100/",
		"\n ASCII Battleship Art - ASCII Art - http://ascii.co.uk/art/battleship",
		"\n Hope you enjoyed it! Read README.md/README.txt for the changelog and future goals.",
		"\n\n Back to menu or exit? (M / E)?\n"]
	fancy_print(credit, flag_)

# Game functions

# TODO: Switch to OOP (Object-Oriented Programming)

# TODO: Add various difficulty levels (no. of moves, movement of ships, how smart the AI is, etc.)

# TODO: Add local "hotseat" multiplayer
# TODO: Add AI vs AI mode (player watches it step by step, and sees both AI's grids)
# TODO: Add local LAN multiplayer
# TODO: Add TCP/IP multiplayer

# TODO: Add profiles

# Generic Functions


def map_gen(size):
	"""Generates map"""
	map_ = []
	for _ in range(size):
		map_.append([" ~"] * size)
	return map_


def base_print(map_, ships_, turns_, flag_):
	"""Basic print statements"""
	def map_print():
		"""Prints the map."""
		for i in map_:
			for j in i:
				fancy_print(j, False)
			fancy_print("\n", False)
	fancy_print(
			"\n\n The map starts from (1, 1) (top-left) and ends at ("
			+ str(len(map_)) + ", " + str(len(map_)) + ") (bottom-right).", flag_)
	fancy_print("\n\n Legend:\n\n     ~ - Calm Water\n     # - Disturbed Water (Fired at)\n     $ - Sunken Ship", flag_)
	fancy_print("\n\n Number of hostile ships left: " + str(len(ships_)), flag_)
	fancy_print("\n Total number of shots: " + str(turns_[0]), flag_)
	fancy_print("\n Number of shots left: " + str(turns_[1]) + "\n\n\n", flag_)
	map_print()
	# DEBUG STATEMENT
	# fancy_print("\n\n" + str(ships_) + "\n\n", False)

# AI function(s)


def ship_gen(size):
	"""AI function, for generating the ships"""
	ships, _ = [], 0
	while _ < int(size * 1.25):
		ship = [int(rand(1, size) - 1), int(rand(1, size) - 1)]  # 1st element - Y-Coordinate; 2nd element -
		# X-Coordinate
		if ship not in ships:
			ships.append(ship)
			_ += 1
	return ships

# Player function(s)


def player_input(map_, ships, turns):
	"""
	Player function for getting the player's input (where the player shot at for now)
	1st value returned - Y-Coordinate
	2nd value returned - X-Coordinate
	"""
	def func(flag1, flag2, flag3):
		"""
			Internal function for adhering to the 'Stay DRY' (Don't Repeat Yourself) rule.
			1st Parameter is for fancy_print()
			2nd is for X-/Y-Coordinate
			3rd is for error start
		"""
		if flag2:
			if flag3 == 0:
				fancy_print(" Fire at column (X-Coordinate):", flag1)
			elif flag3 == 1:
				cls()
				base_print(map_, ships, turns, not flag1)
				cls()
				fancy_print(" Fire at column (X-Coordinate)", not flag1)
				fancy_print(" That's not on the map Captain! (0 < coord <= " + str(len(map_)) + ").", flag1)
		else:
			if flag3 == 0:
				fancy_print(" Fire at row (Y-Coordinate):", flag1)
			elif flag3 == 1:
				cls()
				base_print(map_, ships, turns, not flag1)
				fancy_print(" Fire at row (Y-Coordinate)", not flag1)
				fancy_print(" That's not on the map Captain! (0 < coord <= " + str(len(map_)) + ").", flag1)
		
		coord = int(input(" "))
		if coord < 1 or coord > len(map_):
			raise ValueError
		else:
			return coord - 1
	flag_1, flag_2, g_x, g_y = True, 0, 0, 0
	while flag_1:
		try:
			g_x = func(True, True, flag_2)
			flag_1 = False
		except ValueError:
			flag_2 = 1
	flag_1 = True
	while flag_1:
		try:
			g_y = func(True, False, flag_2)
			flag_1 = False
		except ValueError:
			flag_2 = 1
	return [g_y, g_x]


def turns_gen(size):
	"""
	Generates the number of turns the player has left
	1st value returned - total no. of turns
	2nd value returned - no. of turns over
	"""
	if int(size ** 2) < int(size * 2.5):  # Only true for size == 1, 2
		return [size, size]
	else:
		return [int(size * 2.25), int(size * 2.25)]


def play():
	"""Runs the game"""
	def game_print(print_flag, flag__1):
		"""Dynamic game scene printing"""
		cls()
		base_print(map_, ships, turns, flag__1)
		if print_flag == 0:
			pass
		elif print_flag == 1:
			fancy_print("\n\n Captain! We already shot there!", True)
		elif print_flag == 2:
			fancy_print("\n\n Captain! Hit reported!\n", True)
		elif print_flag == 3:
			fancy_print("\n\n No hit reported Captain!\n", True)
		return player_input(map_, ships, turns)
	
	def game_gen(flag__1, flag__2):
		"""Game generation"""
		cls()
		fancy_print("\n\n Enter the board size:", flag__1)
		if flag__2 == 0:
			pass
		elif flag__2 == 1:
			fancy_print("\n Please enter a valid number! (1 <= size)\n", True)
		elif flag__2 == 2:
			fancy_print("\n Board size too large! (size <= 50)\n", True)
		return input(" ")
	
	flag_1, flag_2 = True, 0
	while True:
		try:
			size = game_gen(flag_1, flag_2)
			if int(size) <= 0:
				raise ValueError
			elif int(size) > 50:
				raise TypeError
			else:
				break
		except ValueError:
			flag_1, flag_2 = False, 1
		except TypeError:
			flag_1, flag_2 = False, 2
	
	map_ = map_gen(int(size))  # The game map
	ships = ship_gen(int(size))  # Contains the coordinates of all hostile ships
	turns = turns_gen(int(size))  # 1st value - total no. of turns; 2nd value - no. of turns over
	shots = []  # Contains the coordinates of wherever the player has fired
	
	# Game start
	flag1, flag2, win_flag = 0, True, 0
	while turns[1] > 0 and len(ships) > 0:  # Making sure there are enough turns and enemy ships
		guess = game_print(flag1, flag2)
		flag2 = False
		if guess in shots:  # Checks whether the player already shot there
			flag1 = 1
		elif guess in ships:  # Hit reported!
			shots.append(guess)
			ships.remove(guess)
			map_[guess[0]][guess[1]] = " $"
			flag1 = 2
		else:  # No hit reported!
			shots.append(guess)
			map_[guess[0]][guess[1]] = " #"
			flag1 = 3
		turns[1] -= 1
	
	# Victory / Loss Screen
	for ship in ships:  # For displaying ships on the map
		map_[ship[0]][ship[1]] == " S"
	if len(ships) != 0:  # Checks if there are ships left
		win_flag = 2
	elif len(ships) == 0:  # No ships left (VICTORY!)
		if turns[1] != 0:  # Checks if turns are left
			win_flag = 1
		elif turns[1] == 0:  # Checks if turns are over
			win_flag = 3
	cls()
	base_print(map_, ships, turns, False)
	if win_flag == 0:
		fancy_print("\n\n Not sure what happened Captain!\n", True)  # ERROR! win_flag doesn't change it's value
	elif win_flag == 1:
		fancy_print("\n\n All hostiles down Captain!\n", True)
	elif win_flag == 2:
		fancy_print("\n\n Ammunition depleted Captain! We need to go back to the harbour!\n", True)
	elif win_flag == 3:
		fancy_print(
				"\n\n All hostiles down Captain! But our ammunition is depleted! We need to go back to the harbour!\n",
				True)
	if win_flag == 1 or win_flag == 3:
		score = (turns[1] - len(ships)) * 100
		fancy_print("\n SCORE: " + score, True)
	sleep(2.5)
	return win_flag
	# Game end


flag, menu_op = True, 0
while True:  # Menu navigation
	menu_flag_1, menu_flag_2 = True, 0
	while menu_op == 0:
		try:
			menu_ans = menu_nav(menu_flag_1, menu_flag_2)
			if int(menu_ans) < 1 or int(menu_ans) > 3:
				raise ValueError
			else:
				menu_op = int(menu_ans)
		except ValueError:
			menu_flag_1, menu_flag_2 = False, 1
	if menu_op == 1:
		cls()
		play()
		cls()
		fancy_print("\n\n Thanks for playing!\n", True)
		credits_(True)
		while True:
			credits_op = input(" ")
			try:
				if credits_op.lower() == "m":
					menu_op = 0
					break
				elif credits_op.lower() == "e":
					menu_op = 3
					break
				else:
					raise ValueError
			except ValueError:
				cls()
				fancy_print("\n\n Thanks for playing!\n", False)
				credits_(False)
				fancy_print("\n Please enter a valid character (M / E)\n", True)
	elif menu_op == 2:
		cls()
		credits_(True)
		while True:
			credits_op = input(" ")
			try:
				if credits_op.lower() == "m":
					menu_op = 0
					break
				elif credits_op.lower() == "e":
					menu_op = 3
					break
				else:
					raise ValueError
			except ValueError:
				cls()
				credits_(False)
				fancy_print("\n Please enter a valid character (M / E)\n", True)
	elif menu_op == 3:
		break

cls()
fancy_print("\n\n Thanks for playing!", True)
fancy_print("\n Cleaning up stuff and exiting.", True)
sleep(2)
fancy_print("\n Bye!", True)
sleep(0.5)
exit()
