# aoe_helper
This program is intended to help the player while playing the game "Age of Empires II: DE".

## About

This program takes partial screenshots of the Ingame UI. After that, letters and number of the screenshot are recognized by a machine learning approach. The recognized characters are used to show the player more information while he is playing the game.

### Technologies used
These technologies are used to develop this program:
- Java
- [JNA](https://github.com/java-native-access/jna) (used to make a click-through window)
- [jnativehook](https://github.com/kwhat/jnativehook) (used for global keyboard listening)
- [tess4j](https://github.com/nguyenq/tess4j) (used to recognize characters)

## 🎮 Demo

An execubale jar file is in the folder AoE_Helper/dist.
It's optimized for Windows and a resolution of 1920x1080. To end the program simply click the "." key. The demo was tested ingame with the "Vietnamese" civilization, the user is notified when he needs to make houses.


## 🌈 Screenshots

Ingame
<p align="center">
  <img src="https://github.com/rayo3/aoe_helper/blob/master/AoE_Helper/screenshots/ingame.jpg">
</p>

Not ingame
<p align="center">
  <img src="https://github.com/rayo3/aoe_helper/blob/master/AoE_Helper/screenshots/not_ingame.jpg">
</p>

## 🔨Development

### WIP
- "Build House" message (if pop + 2 >= max_pop)

### TODO
- Show BOs and common strategies for civilizations
- Show amount of trade cards / fishing boats
- Use point reading to guess when someone presses "age up" technologies
- Add new hotkeys: Go to nearest stone/gold, produce continuously specific unit...
