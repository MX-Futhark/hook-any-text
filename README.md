# Hook Any Text

HAT is a plugin for Cheat Engine that automatically converts hexadecimal data into readable strings.
This project aims at providing a way to hook text when other widely used text hookers fail to do so.
Therefore, it might not be as user-friendly as what is expected of such tools, and the result might not be as clean as you would expect.
However, you WILL be able to hook the text of any game, even on emulators.


## You are a user

For all downloading and installation instructions, please visit the wiki here: https://github.com/MX-Futhark/hook-any-text/wiki.


## You are a developer

You should also visit the wiki first.
Once this is done, read LICENSE (TL;DR: MIT License. If you're going to use stuff from this project, feel free to do so but give credit where credit is due.)
Then, here are some more stuff for you:


### How it works

selectionConverter.lua is run when Cheat Engine starts. The hooking occurs when a zone in the memory is selected.
Changes in the selected zone cause the hexadecimal data to be piped into a process that does the actual conversion and sets the result to the clipboard.


### General organization

The Lua file (automatically run when Cheat Engine is started) is placed in the lua directory.

The actual conversion of hexadecimal strings is done in Java, and goes into the src directory.

The conversion is not done directly in lua because:

 * Encoding support in Lua is minimal, therefore the language is not the most fitting for this project.
 * It would be more difficult to organize everything.


### Convertion process

The hooking and conversion process can be summarized by the following steps:

1. A hexadecimal string fed to the Java program is first cleaned of all unnecessary parts, such as sequences of zeroes, and separated into as many clean chunks.
2. These chunks are given a mark representing how likely they are to be an actual string.
3. They are then converted into as many readable strings.
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
        | |-[game1]
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
        |-utf16  (contains tests for games using UTF-16)
          |-[gameA]
          | |-input
          | | |-0001.txt
          | | |-0002.txt
          | | |-...
          | |-expected_output
          |   |-0001.txt
          |   |-0002.txt
          |   |-...
          |-[gameB]
          |-...

          
The main test class hextostring.tests.TestsLauncher assumes that all .txt files are encoded in UTF-8, without the BOM.