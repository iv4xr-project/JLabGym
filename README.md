# <img src="./docs/iv4xr_logo_1200dpi.png" width="20%"> <img src="./docs/logo.png" width="8%"> JLabGym

This package provides a Java-based environment that will allow you to use the game [Lab Recruits](https://github.com/iv4xr-project/labrecruits) as an 'AI Gym'. An _AI Gym_ is an environment where you can try out your AI, or whatever algorithm X, to perform a certain task in the environment. JLabGym provides a set of methods to control the Lab Recruits game and to obtain its internal state information. The Lab Recruits game itself also allows you to design your own game-level through a simple CSV file, and hence you can design your own specific level layout, puzzles, hazards, and goals that your AI can try.

### Using JLabGym

#### Get Lab Recruits

   <img src="./docs/LRSS1.png" width="48%"><img src="./docs/LRSS3.png" width="50%">

You need to get its executable, here.....

For Mac: put `LabRecruits.app` in `gym/Mac/bin`. For Windows: put the files including `LabRecruits.exe` in `gym/Windows/bin`.

#### Eclipse

   This will allow you to run/modify/rerun the demo classes. Import the project into Eclipse as a **maven project**. The demo classes are in `src/test/java/agents/demo`. You can run them as junit tests.

#### Maven

   This is if you just want to check that the project builds and that all its tests pass.
   Just do `mvn compile` and `mvn test` at the project root.

### Other documentations

* For iv4xr team: [World Object Model](./docs/Observation.md)
* For iv4xr team: [where to find goals and tactic](./docs/LRtestingLib.md)
* For others: [basic interface to control _Lab Recruits_](./docs/BasicInterface.md)

### What's in the package

* `./src` the source files. It follows Maven's convention, so the root of the source files is in `src/main/java` and the root of tests' source files is in `src/test/java`.
* `./gym` contains the binary of the _Lab Recruits_ game (macos and windows).
* `./src/test/resources/levels` contain sample level definitions for _Lab Recruits_.

### Copyright and License

Copyright (c) 2021, Utrecht University (Department of Information and
Computing Sciences).

License: GNU LGPL.

### Contributors

**Computer Science students from Utrecht University:**
Adam Smits,
August van Casteren,
Bram Smit,
Frank Hoogmoed,
Jacco van Mourik,
Jesse van de Berg,
Maurin Voshol,
Menno Klunder,
Stijn Hinlopen,
Tom Tanis.
**Others:** Wishnu Prasetya, Naraenda Prasetya.
