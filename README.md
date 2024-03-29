# aoe-helper
This program is intended to help the player while playing the game "Age of Empires II: DE".

## About

This program takes partial screenshots of the Ingame UI. After that, letters and number of the screenshot are recognized by a machine learning approach. The recognized characters are used to show the player more information while he is playing the game.

### Technologies used
These technologies are used to develop this program:
- Java
- [JNA](https://github.com/java-native-access/jna) (used to make a click-through window)
- [jnativehook](https://github.com/kwhat/jnativehook) (used for global keyboard listening)
- [tess4j](https://github.com/nguyenq/tess4j) (used to recognize characters)
- [json-simple](https://github.com/fangyidong/json-simple) (used to parse json files)
- [marvinproject](https://github.com/gabrielarchanjo/marvinproject) (used for image processing)
- [ini4j](https://github.com/facebookarchive/ini4j) (used to save/load settings)

### Copyright notice
Age of Empires II: Definitive Edition © Microsoft Corporation. **AoEHelper** was created under Microsoft's "[Game Content Usage Rules](https://www.xbox.com/en-us/developers/rules)" using assets from Age of Empires II: Definitive Edition, and it is not endorsed by or affiliated with Microsoft.

## 🎮 Demo

An execubale jar file is in the folder "AoE_Helper/dist". Ingame, the player is notified when he needs to make houses, also messages appear to guide the player so that he masters the "Fast Castle" build order. The demo was tested ingame with a resolution of 1920x1080 pixels (although any resolution might work, since the In-game HUD resolution is independent of the game resolution) and an In-game HUD Scale of 100%. If you are not ingame, the program shows the text "Not ingame". To end the program simply click the "." key or use the GUI.
### Requirements
- Windows
- At least [JRE 1.8](https://www.java.com/de/download/)

## 🌈 Screenshots

Ingame
- An image of a house is shown because the population limit is almost reached.
- Different messages of the fast castle build order are shown dependent on the villager amount.
<p align="left">
  <img src="https://raw.githubusercontent.com/rayo3/aoe_helper/master/AoE_Helper/screenshots/ingame.jpg">
</p>

Not ingame
- The message "Not ingame" is shown if outside of a game.
<p align="left">
  <img src="https://raw.githubusercontent.com/rayo3/aoe_helper/master/AoE_Helper/screenshots/not_ingame.jpg">
</p>

GUI
<p align="left">
  <img src="https://raw.githubusercontent.com/rayo3/aoe_helper/master/AoE_Helper/screenshots/gui.png">
</p>

## 🔨Development

### Done
- Show house image if player is two or less units away from max population
- Show messages to master the "Fast Castle" build order

### WIP
- Show more BOs and common strategies for civilizations
- Use point reading to guess when someone presses "age up" technologies

### TODO
- Show amount of trade cards / fishing boats
- Add new hotkeys: Go to nearest stone/gold, produce continuously specific unit...
