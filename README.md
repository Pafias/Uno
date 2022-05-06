# Uno
UNO card game in Minecraft

This is not a mod. This is a server plugin.

Made for **Paper 1.16.5** - probably not compatible with other versions. (Maybe i'll make support for other versions later)

You need ProtocolLib in your server
#
Before starting a game make sure you setup the environment properly:

You will need seats for the players. You can save their location by standing on the seat and using /uno setseat <playernumber> where playernumber is, well, the player number, so like 1, 2, 3, 4
  
You will also need to place an itemframe somewhere in front of each seat. That is where the discard pile card will appear. Use /uno setiframe <playernumber> while looking at the itemframe to save it's location.

To draw a card just place a button somewhere reachable to all players.
  
Confused? Want to have an example? I recommend downloading the map world and config.yml as explained below, to have an idea of how it works.
  
#
  
Example world: [download world](http://pafias.tk/minecraft/uno.zip) or [download schematic file](http://pafias.tk/minecraft/uno.schem) (not recommended because coordinates may differ from what is set in the config.yml)
  
Example config.yml: [download](http://pafias.tk/minecraft/config.yml) (place it in the Uno folder in your plugins folder)

Plugin jar file can be found in the releases tab or click [here](https://github.com/Pafias/Uno/releases/latest/download/Uno-1.0-SNAPSHOT.jar) if you're lazy

#

The plugin only has 1 command: /uno
  
Running the command will show the subcommands with a description of them
