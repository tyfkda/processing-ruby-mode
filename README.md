Ruby Mode for PDE (Processing)
==============================

**Working in progress**

This is additional mode for PDE, [Processing](http://www.processing.org/) IDE.
You can write graphical application in Ruby.

Code to run application is taken from [ruby-processing](https://github.com/jashkenas/ruby-processing).

# Development
## Requirements
* Java tools: `javac`, `jar`
* Processing runtime jars: `core.jar`, `pde.jar`

## How to build
* Type `make` at the repository root directory.
* `RubyMode.jar` will be created

## How to install
+ Create `RubyMode` directory in `modes` directory in Processing working directory:
  * MacOSX: `~/Documents/Processing/modes/`
  * Windows: `%homepath%\Documents\modes\`
  * Linux: `~/sketchbook/modes/`
+ Copy all files under `others/` directory into created `RubyMode` directory
+ Copy `RubyMode.jar` into `RubyMode/mode` directory
+ Reboot Processing

## How to use Ruby mode in PDE
* Choose `Ruby` from mode pull down menu in PDE
