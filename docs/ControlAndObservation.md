# Controlling and Observing Lab Recruits

This document explains JLabGym's APIs and relevant data structures to control the Lab Recruits game, and to observe its state.


### Control

To control a running instance of Lab Recruits you need an instance of [`LabRecruitsEnvironment`](./src/main/java/environments/LabRecruitsEnvironment.java). See [README.md](./README.md) for an example of how to create it and how to bind it to a running Lab Recruits.

The following methods/APIs are available to control and observe the Lab Recruits' instance to which your `LabRecruitsEnvironment`; let `env` be this instance:

1. `LabWorldModel env.observe(String agentId)`

   This returns information on what the player character currently sees. The observation is given as an instance of `LabWorldModel`.

   The method requires a so-called _agentId_. When a game-level is loaded into Lab Recruits, typically the level will have one or more 'player characters' that the human player can control. Only one human player can play the game at the same time, but the human can switch between different characters (if there are more than one). These player-characters can also be controlled programatically from JLabGym. Each of those characters is identified by its _id_, which is what we mean by _agentId_ we mentioned above.

   This id can be found in the level-definition file of the game-level that you loaded into Lab Recruits.

1. `env.moveToward(String agentId, Vec3 agentLocation, Vec3 destination)`

   This will move the agent specified by the given id, in a straight line towards the given destination. This will move the agent as far as its speed allows (by default 0.13 distance unit). This distance should be obstacle free (the agent cannot move through obstacles like walls). To cover a greater distance you will have to invoke `moveToward` multiple times.

   Note that this method requires the agent's current position, which you can obtain e.g. as follows:

  ```java
  LabWorldModel wom = env.observe(agentId) ;
  Vec3 agentPosition = wom.getFloorPosition() ;
  ```

  We also have `wom.position`, that will give the position of the agent's body's center point, whereas `wom.getFloorPosition()` gives the agent's position, normalized to its position on the floor it stands on.

  Also note that 'traveling', even if for just a small distance, takes time. The method `moveToward` is however not synchronized (that is, the method returns after sending the instruction. It does not actually know when the agent has completed the instruction). So you need to add a delay between `moveToward` and the next instruction, e.g.:

  ```java
  env.moveToward(agentId,agentPosition,destination);
  Thread.sleep(50); // give some delay, then move again:
  wom = env.observe(agentId); // should give what the agent observes after finishing the moveToward
  ```

  Location/positions are given as instance of the class [`Vec3`](https://github.com/iv4xr-project/aplib/blob/master/src/main/java/eu/iv4xr/framework/spatial/Vec3.java), which specifies a coordinate in a 3D space. An instance of `Vec3` can be thought as a tuple _(x,y,z)_ with the usual 3D interpretation. Do note that _y_ denotes a position along the vertical axis, whereas _x_ and _y_ are the coordinate/position on the horizontal plane of a 3D space.

1. `LabWorldModel env.interact(String agentId, String targetId, String interactionType)`

   This returns an instance of `LabWorldModel`. The `targetId` is the id of the entity that you want to interact with. Only buttons are interactable in Lab Recuits, so this must be the id of some button. Again, this can be found in the level file of the game level that you loaded into Lab Recruits.

  The parameter `interactionType` is ignored.

  Note that to interact with a button, the agent needs to stand close enough to the button (<= 1.0 distance unit).

  The method returns the observation _after_ the interaction.

1. `env.close()`.

   Not an instruction for Lab Recruits. This is to close `env` if you don't need it anymore. Among other things, this will close the TCP socket it uses to communicate with Lab Recruits. To also close the instance of Lab Recruits, see the example in [README.md](./README.md).  


### World Object Model: structural represention of what an agent sees.

Recall that the methods `observe()` and `interact()` of the class [`LabRecruitsEnvironment`](./src/main/java/environments/LabRecruitsEnvironment.java) return what your agent currently sees. In the code snippet below, `wom1` contains what the agent sees before the interaction with a button, and `wom2` contains what it observes after the interaction.

```java
// let environment be your instance of LabRecruitsEnvironment that binds
// to a running instance of Lab Recruits.
LabWorldModel wom1 = environment.observe(agentId) ;  
LabWorldModel wom2 = environment.interact(agentId,buttonId,"");
```

Observation is captured as an instance of the class `LabWorldModel`, which in turn is a subclass of `eu.iv4xr.framework.world.WorldModel`. An instances of `WorldModel` is also called _World Object Model_ (WOM) as it **structurally** describes what the game-world looks like from the agent's eye (as opposed to representing observation by images) .

A WOM of _Lab Recruits_ has the following fields:


1. `String agentId`: the agent's _id_.

1. `Vec3 position`: the agent's current _position_ (its center point position). You can also use `wom.getFloorPosition()` to get the agent's position when projected to the floor it is on.

1. `Vec3 extent` describes the agent's dimension. It is a tuple (a,b,c) of non-negative values. It means that agent's width, length, and height are respectively 2a, 2b, and 2c.

1. `int health` and `int score` contain the agent's current health point and score.

1. `long timestamp` denotes when the WOM is taken.

1. `Map<String, WorldEntity> elements`

   This a collection of _in-game entities_ that the agent currently sees. The collection is represented as a a mapping from entity-id to the corresponding entity. The type of the entity is actually `LabEntity` which is a subclass of `WorldEntity`.

   Use the method `wom.getElement(id)` to obtain the element with the specified id. It returns null if an entity with that id cannot be found in `elements`. Below we will explain how to know the id of an entity.

1. `int[] visibleNavigationNodes`

    This array describes the fragment of the world that the agent current see.
    You do not actually get a set of physical 3D points which are currently visible. This set is infinitely large; we can't construct it. Instead, the information is provided abstractly (so, there is some loss in information) as a graph of connected triangles, that together describe areas of the world that are _walkable_ by the agent, and are currently _visible_ to it. More about this is explained here: [Navigation in the world of Lab Recruits](.....)


### Obtaining information on game-entities' properties

Recall that when you ask for an observation (e.g. through `env.observe()`), the World Model that you obtain also contains the field `elements` containing all the in-game objects/entities that the agent currently see.

An in-game entity is represented by an instance of `LabEntity` which in turn is a subclass of  `eu.iv4xr.framework.world.WorldEntity`. This structure contains information on the agent's properties/state. The following fields/getters are available:

1. `String id`: an _id_ that uniquely identify the entity.

   When a game-level is loaded into Lab Recruits, all in-game entities have a unique id so that you can uniquely address them from JLabGym. For entities such as buttons, doors, and goal-flags the ids are defined in the level-definition file of the game-level that you loaded. For entities such as fire and furniture, the ids are generated.

1. `String type`: a string naming the entity _type_ (e.g. an entity with id "d10" could be of type "door").

1. `long timestamp`: a time stamp of when the state described by this `LabEntity` is sampled.

1. `Vec3 position`: the position of the entity's center point.

1. `Vec3 getFloorPosition()`: use this method if you want to obtain the entity's position projected to the floor it is on.

1. `Vec3 extent` describes the agent's dimension. It is a tuple (a,b,c) of non-negative values. It means that entity's width, length, and height are respectively 2a, 2b, and 2c.

1. `Map<String, Serializable> properties` contains information about the entity's properties. Each property is essentially a pair of _(name,value)_ specifying the name of the property and its value.

  For example, buttons has a property named "isOn" whose value is of type boolean. Doors has a property named "isOpen" whose value is also of type boolean.

1. `Serializable getProperty(String name)`: traverse the field `properties` above to get the value of the named property. It returns null if the property is not found.

1. `boolean getBooleanProperty(String name)`: similar as `getProperty` but a convenience to obtain the value of a boolean property.

1. `int getIntPorperty(String name)`: similar as above.

1. `Map<String, WorldEntity> elements`: containing this entity's sub-elements. E.g. if this entity represents a bag, then this field would contain the items which are in the bag. The collection is represented as a mapping for ids to the corresponding entities.
