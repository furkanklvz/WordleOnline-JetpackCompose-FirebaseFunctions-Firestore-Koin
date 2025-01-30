# **Wordle Online 🎮**

**Wordle Online** is an online version of the popular Wordle game built with **Jetpack Compose** for Android and **Firebase Functions** for the backend. This project was developed to showcase the core mechanics of the Wordle game and is not intended for production release.
---


## 📱 **Gameplay**

![wordle-online-edited-record-gif](https://github.com/user-attachments/assets/42cc2fa1-e556-4044-bec7-e7c20652fbd9)

---

## 🚀 **Features** 

- **Custom Keyboard**: An in-app keyboard tailored to the game's needs, making gameplay smoother.  
- **Color-Coded Feedback**: 
  - **Yellow**: Letters in the guess that are in the target word but in a different position.
  - **Green**: Letters in the correct position and part of the target word.
  - **Grey**: Letters that are not in the target word at all are highlighted on the keyboard.
- **Timed Rounds**: Each player has a **40-second** timer to make their guess. ⏳
- **Multiplayer**: Compete in real-time against others and test your Wordle skills. 🏆

---

## ⚙️ **Technologies Used**

- **Jetpack Compose**: For building modern and responsive UI. 📱  
- **Firebase Firestore**: For cloud storage and real-time database synchronization. 🔥  
- **Firebase Functions (TypeScript)**: For game logic, real-time gameplay, and server-side functionality. 🖥️  
- **Koin**: For dependency injection, managing app components. 🔌  
- **Coroutines & Flows**: For handling asynchronous operations and data flow. 🌊

---


## 🎯 **How It Works**

1. **Gameplay**: 
   - Players have **6 attempts** to guess a 5-letter word. 
   - After each guess, feedback is provided through colored tiles.
   
2. **Backend**: 
   - Firebase Functions handle the game logic, ensuring players can play against each other in real-time.

3. **UI/UX**: 
   - The **in-app keyboard** and dynamic feedback system enhance the gameplay experience. The **timer** keeps the game pace quick and exciting!

---
