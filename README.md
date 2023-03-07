This is a simple plugin that fixes issues with MythicMobs.

The plugin is compatible with Paper and it's forks.

Currently fixed issues:
 - If you add the following options to a mob: **Despawn: false** or **Despawn: persistent** and the mob dies, there is a possibility the mob to become vanilla and drop items that you didn't set, the onDeath sklls will never execute and the MythicMobDeathEvent that other plugins might use like MythicDungeons will never fire.

The names of the keys that are being used in the persistent data continers, were discovered with the use of the minecraft command /data. And the check for the fix was created with the help of this simple plugin [Debugger](https://github.com/rafael5gr2/Debugger)

**The plugin might not fully fix the issues that are displayed above or it might introduce other ones! So use it at your own risk!**

To get the plugin you can just compile it with the build task of gradle.
