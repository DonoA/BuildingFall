# BuildingFall
Bukkit plugin that makes buildings fall over

The basic commands are:

- `createfall <name>`: Gives a wand to select the "building" to make fall over
- `deletefall <name>`: Deletes the fall selection
- `startfall <name>`: Makes the building fall over
- `stopfall <name>`: Stops a running fall
- `resetfall <name>`: Resets the building to before the fall took place

The challenging part of this plugin was the math to rotate the building as it fell. At the time, I had not taken any 3D trigonometry or linear algebra so the math behind making a 3D rectangle rotate in real time took a while to get my head around.
