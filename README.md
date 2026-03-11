# FruitApp User Guide
FruitApp is a mobile app designed for use with its [companion Arduino projects](https://github.com/Ianos828/arduino-projects).
The project was made to take measurements of fruits and their various attributes, in order to
ascertain their freshness. Below is a user guide on how to use FruitApp.

## Quick Start
1. Ensure you have Android `26` or higher.
2. Download the latest release from [here](https://github.com/Ianos828/FruitApp/releases).
3. Open the `apk` file and follow the instructions to install the app. You should see something like this when opening the app.

![FruitApp GUI](/docs/Ui.png)

## Features

> [!NOTE]
> **Notes about the app:**
> - The app only functions when connected to the same WiFi network as the Arduinos in this project.
> - When the app fails to receive information from the Arduino, it will use a dummy value in its place.
### Taking a Measurement
On the main screen, click on the `Take Measurement` button. The measurement will display on your screen.

### Saving a Measurement
After taking a measurement, you can choose to save or discard the measurement. Saving the measurement
will save the data on your device.

### Viewing Past Measurements
On the main screen, click on the `View Past Measurements` button. Past saved measurements can then
be viewed. On each measurement, clicking on the dropdown menu will reveal more information about the
measurement.

### Deleting Measurements
On the history screen, selecting the dropdown menu and clicking on the trash icon will delete that
specific measurement from memory. Clicking on `Clear All History` button will delete all past entries.