                          Hook Any Text

  What is it?

  -----------
  HAT is a plugin for Cheat Engine that automatically converts
  hexadecimal data into readable strings. This project aims at
  providing a way to hook text when other widely used text hookers
  fail to do so, particularly for emulated games.


  The Latest Version
  ------------------

  Details of the latest version can be found on the Hook Any Text
  project page under https://mx-futhark.github.io/hook-any-text.


  Licensing
  ---------

  Please read LICENSE.txt.


  Installation
  ------------

  1. Make sure that a version of Cheat Engine (6.2 or over) is
     installed on your computer.

  2. Make sure that Java (7 or over) is installed on your computer.

  3. Copy/Paste selectionConverter.lua and HexToString.jar in the
     autorun subfolder of Cheat Engine's installation Folder.


  User Manual
  -----------

  The instructions below assume a certain level of ease with
  Cheat Engine. For more detailed instructions, please visit the wiki
  of the project at https://github.com/MX-Futhark/hook-any-text/wiki.

  1. Open the game.

  2. Open Cheat Engine.

  3. Attach CE to the process of the game.
     If you are using an emulator, make sure memory mapping is enabled.

  4. Copy part of the dialog of the game manually and convert the
     string in hexadecimal using the same encoding as the game.

  5. Pause the game if you are using an emulator.

  6. Scan the process for an array of byte, copying the hexadecimal
     string you obtained at step 4 in the "value" text box.

  7. Unpause the game if you paused it.

  8. Browse the memory region of a fitting address in the result list.

  9. Select the part of the hexadecimal data you wish to convert.

  Once this is done, just play the game. New strings will be put in
  the clipboard.


  Troubleshooting
  ---------------

  Please visit the wiki of the project if you encounter any problem:
  https://github.com/MX-Futhark/hook-any-text/wiki.


  Author
  --------------

  Creator: Maxime "Futhark" Pia.


  Reporting Bugs
  --------------

  If you encounter an unexpected behaviour while using HAT, please
  submit an issue at https://github.com/MX-Futhark/hook-any-text/issues.
  In the issue message, precise the name of the game, the emulator and
  its version number if you used one, and the hexadecimal data and the
  resulting string if the issue is about a conversion problem.
  Please make sure there isn't already an issue covering your problem
  first.


  Requesting Features
  -------------------

  If you would like HAT to do something it currently cannot, feel free
  to send an e-mail to mxfuthark@gmail.com.
  Please first check said feature is not already included in a newer
  version at https://mx-futhark.github.io/hook-any-text,
  or planned at https://github.com/MX-Futhark/hook-any-text/TODO.


  Participating
  -------------

  If you wish to participate to the development of the project, please
  fork the repository at https://github.com/MX-Futhark/hook-any-text
  and submit your pull requests.
  If you plan on writing a new feature entirely, please first discuss
  it with the owner of the repository at mxfuthark@gmail.com.


  Changelog
  ---------

  0.4.1 - 2015-29-08
   - Fixed rejection of valid Shift-JIS strings
   - Added input preprocessing before encoding recognition

  0.4.0 - 2015-16-08
   - Added encoding recognition.
   - Set default encoding to auto-recognized.
   - Improved Japanese recognition.

  0.3.0 - 2015-15-08
   - Added support for games using UTF-8.

  0.2.0 - 2015-15-08
   - Added support for games using UTF-16 Big Endian.
   - Added support for games using UTF-16 Little Endian.

  0.1.1 - 2015-13-08
   - Fixed bug raising an exception due to empty strings.

  0.1.0 - 2015-08-12
   - Added support for games using Shift-JIS.
   - Added debug mode.
