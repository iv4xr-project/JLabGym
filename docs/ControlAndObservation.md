### Controlling and observing Lab Recruits

To control a running instance of Lab Recruits you need an instance of [`LabRecruitsEnvironment`](./src/main/java/environments/LabRecruitsEnvironment.java). See [README.md](./README.md) for an example of how to create such an instance and to bind it to a running Lab Recruits.

The following methods are available to control and observe the Lab Recruits' instance to which your `LabRecruitsEnvironment`; let `env` be this instance:

* `env.observe(String agentId)`; return an instance of `LabWorldModel`. It needs the Lab Recruits' id of the agent (the player character) that you want to control. This id can be found in the level file of the game-level that you loaded into Lab Recruits.

* `env.moveToward(String agentId, Vec3 agentLocation, Vec3 destination)`. This will move the agent specified by the given id, in a straight line towards the given destination. This will move the agent as far as its speed allows (by default 0.13 distance unit). This distance should be obstacle free (the agent cannot move through obstacles like walls). To cover a greater distance you will have to invoke `moveToward` multiple times.

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

* `env.interact(String agentId, String targetId, String interactionType)`; return an instance of `LabWorldModel`. The `targetId` is the id of the entity that you want to interact with. Only buttons are interactable in Lab Recuits, so this must be the id of some button. Again, this can be found in the level file of the game level that you loaded into Lab Recruits.

  The parameter `interactionType` is ignored.

  Note that to interact with a button, the agent needs to stand close enough to the button (<= 1.0 distance unit).

  The method returns the observation _after_ the interaction.

* `env.close()`. Not an instruction for Lab Recruits. This is to close `env` if you don't need it anymore. Among other things, this will close the TCP socket it uses to communicate with Lab Recruits. To also close the instance of Lab Recruits, see the example in [README.md](./README.md).  


### World Object Model: structural represention of what an agent sees.

Recall that the method `observe()` and `interact()` of the class [`LabRecruitsEnvironment`](./src/main/java/environments/LabRecruitsEnvironment.java) return what your agent currently sees. In the code snippet below, `wom1` contains what the agent sees before the interaction with a button, and `wom2` contains what it observes after the interaction.

```java
// let environment be your instance of LabRecruitsEnvironment that binds
// to a running instance of Lab Recruits.
LabWorldModel wom1 = environment.observe(agentId) ;  
LabWorldModel wom2 = environment.interact(agentId,buttonId,"");
```

Observation is captured as an instance of the class [`LabRecruitsEnvironment`](./BasicInterface.md) provides basic methods, such as `observe(agentId)` and `moveToward(agentId,p,q)` to control the _Lab Recruits_ game. In addition to executing the command in the game, these commands also return what the in-game agent (identified by _agentId_) observes. This observation represented as an instance of the class `LabWorldModel`, which in turn is a subclass of `eu.iv4xr.framework.world.WorldModel`. Instances of `WorldModel` is also called _World Object Model_ (WOM) as it **structurally** (so, not visually) describes what the game-world looks like from the agent's eye.

A WOM of _Lab Recruits_ has the following structure:

* the agent's _id_.
* the agent's current _position_ (center position).
* the agent's current _velocity_.
* the agent's bounding box.
* _timestamp_ denoting when the WOM is taken.
* a collection of _in-game entities_ that the agent currently sees.
* additionally also the fragment of the in-game world that the agent current see.

   In the case of _Lab Recruits_, the game does not send full description of this, but instead it only sends the _navigable part_ of the world that the agent currently sees. In other words, the game sends back information about which part of the in-game floor is visible, but it does not send any information on the walls that surround the agent. Information about the floor in enough to allow your AI to figure out how to navigate from one place in the virtual world to another, though the AI will not know if it is surrounded by walls, or by abyss.

   The data on the visible navigable part of the world is encoded in so-called _navigation-graph_. The 'floor' is divided into adjacent polygons (usually triangles). A navigation graph consists of the corners of these polygons, and edges representing how these corners are connected to each other. It is up to your AI how to use this information.

An in-game entity is represented by an instance of the class _WorldEntity_. Each has the following information:

* an _id_ that uniquely identify the entity.
* a string naming the entity _type_ (e.g. an entity with id _d10_ could be of type _door_).
* _timestamp_.
* the entity's _position_ (center position).
* the entity's _velocity_.
* the entity's bounding box.
* a boolean indicating whether the entity is _interactable_.
* a boolean indicating whether the entity is _dynamic_. An entity is 'dynamic' if its state can change at the runtime.
* a list of _properties_, each is a pair of (_n_,_v_) where _n_ is the property name and _v_ is the property value.
* a collection of other in-game entities that is a part of this entity (e.g. if this entity is represent an in-game bag containing other stuffs).
