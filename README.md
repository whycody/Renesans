![Grafika](https://github.com/whycody/Renesans/assets/36113911/37c66ba6-de45-49c6-b3ec-fd5245688e73)

# Renesans
Renesans is a mobile application created for the "Szlakiem Mikołaja Kopernika" programming competition, aimed at bringing users closer to the times in which Nicolaus Copernicus lived. The app features short articles about people who lived during the Renaissance, important works, and significant events of the era.

## Features

- **Short articles**: Discover creators such as Nicolaus Copernicus and Leonardo da Vinci and bring their works from past eras closer to you.
- **Interactive trail**: Take part in an interactive trail that will guide you through the history of figures like Nicolaus Copernicus.
- **Map with Pins**: Explore cities scattered across Poland and their most hidden spots.
- **Old photographs**: Visit buildings that have been captured in photographs from the twentieth century.

## Used technologies

- **Programming Language**: Kotlin. I decided to create the project programming in Kotlin, as it has increasingly replaced Java in recent years. It offers several advantages, including the adoption of new technologies (such as Koin, a dependency injection library) and the ability to write significantly less code.

- **Database**: Firebase, Realm. Firebase served as the online database, while Realm served as the object-oriented offline database. With user consent, images in selected quality are downloaded directly to the device.

- **Design Pattern**: MVP (Model View Presenter). To maintain code cleanliness, I tailored my project to one of the most popular design patterns, MVP. While this required writing more code, it makes the software more readable for other programmers and more accessible for its future development.

## Screenshots

![screenshots](https://github.com/whycody/Renesans/assets/36113911/8e0f8a9b-3e68-47f9-a897-be2a92911e48)

## Installation

To clone and run this project on your local machine:

1. Clone the repository:
   ```sh
   git clone https://github.com/whycody/Renesans.git
   ```
2. Open the project in Android Studio:  
   - Launch Android Studio.  
   - Select "Open an existing project".  
   - Navigate to the folder containing the cloned repository.  

4. Install the required dependencies:  

    - Android Studio will automatically download and install all necessary dependencies.

5. Run the app on an emulator or physical device:  

    - Choose a run/debug configuration.  
    - Click the "Run" button (green arrow).  
