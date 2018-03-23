# Hook Any Text

HAT is a plugin for Cheat Engine that automatically converts hexadecimal data into readable strings.
This project aims at providing a way to hook text when other widely used text hookers fail to do so.
Therefore, it might not be as user-friendly as what is expected of such tools, and the result might not be as clean as you would expect.
However, you should be able to hook the text of a vast range of games, particularly on emulators.


## You are a user

For all downloading and installation instructions, please visit the wiki here: https://github.com/MX-Futhark/hook-any-text/wiki.


## You are a developer

You should also visit the wiki first.
Once this is done, read LICENSE (TL;DR: MIT License. If you're going to use stuff from this project, feel free to do so but give credit where credit is due.)
Then, here are some more stuff for you:


### Build instructions

I recommend Eclipse or Netbeans to use with this project.  
The entry point of the main program is hextostring.Main. Change command line options as necessary in your run configurations.  
The entry point of the test program is hextostring.tests.TestsLauncher. Command line options can be used to restrict the tests to be run.  


I would recommend creating the following run configuration for building and testing the project (In Eclipse Mars):

1. Right click on the project -> Run As -> Maven build...
2. Type the following content in "Goals": clean install test-compile -DskipTests exec:java -Dexec.mainClass=hextostring.tests.TestsLauncher -Dexec.classpathScope="test"


### How it works

selectionConverter.lua is run when Cheat Engine starts. The hooking occurs when a zone in the memory is selected.
Changes in the selected zone cause the hexadecimal data to be piped into a process that does the actual conversion and sets the result to the clipboard.


### General organization

This project uses Maven and respects the usual conventions of a Maven project.  
The Lua file (automatically run when Cheat Engine is started) is placed in src/main/lua.  

The conversion is not done directly in lua because:

 * Encoding support in Lua is minimal, therefore the language is not the most fitting for this project.
 * It would be more difficult to organize everything.


### Convertion process

The hooking and conversion process can be summarized by the following steps:

1. A hexadecimal string fed to the Java program is first cleaned of all unnecessary parts, such as sequences of zeroes, and separated into as many clean chunks. Substitutions are made if required by the user.
2. These chunks are given a mark representing how likely they are to be an actual string.
3. They are then converted into as many readable strings. Substitutions are made if required by the user.
4. These strings are also given a mark to judge how likely they are to be natural language.
5. Strings having received a low mark are eliminated.
6. Strings deemed valid are cleaned of their formatting clues (for example, markup indicating furigana)
7. The survivors are concatenated and the result is set to the clipboard.


### Tests

A test consists of an input and a corresponding expected, manually written output.  
Every input file is converted and compared to the expected output.


Test files are put into the tests directory. Its architecture is as follows:

        tests
        |-sjis (contains tests for games using Shift JIS)
        | |-handmade (contains tests crafted without relying on a game)
        | |-[game1]
        | | |-cmd.txt (contains description of options optimized for this game)
        | | |-input
        | | | |-0001.txt
        | | | |-0002.txt
        | | | |-...
        | | |-expected_output
        | |   |-0001.txt
        | |   |-0002.txt
        | |   |-...
        | |-[game2]
        | |-...
        |-utf16 (contains tests for games using UTF-16)
        | |-[gameA]
        | | |-cmd.txt
        | | |-input
        | | | |-0001.txt
        | | | |-0002.txt
        | | | |-...
        | | |-expected_output
        | |   |-0001.txt
        | |   |-0002.txt
        | |   |-...
        | |-[gameB]
        | |-...
        |-utf16-le (contains tests for games using UTF-16 Little Endian)
        | |-...
        |-utf8 (contains tests for games using UTF-8)
          |-...

          
The main test class hextostring.tests.TestsLauncher assumes that all .txt files are encoded in UTF-8, without the BOM.


### Manual integration testing

It is possible it avoid going through the pain of opening a game in Cheat Engine to test `src/main/lua/selectionConverter.lua` by running `src/test/lua/selectionConverter.lua` in a terminal and simulating hex selections through it. More instructions are provided directly in this file.

Note that this shortcut does not replace a full end-to-end test, but it is handy to reduce the error feedback cycle when working on `selectionConverter.lua`.
