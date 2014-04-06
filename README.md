Sink-Plugins
==============
[![Build Status](http://ci.static-interface.de/job/Sink-Plugins/badge/icon)](http://ci.static-interface.de/job/Sink-Plugins/)

For more information, visit <a href="http://dev.bukkit.org/bukkit-plugins/sink-plugins">the BukkitDev page</a>.

License
==============
This work is licensed under an GNU General Public Licenese v3. For more information, read the LICENSE file.


Coding Standards and Rules
==============
* Each public method should have a javadoc comment
* JavaDoc, comments, commit message etc. must be of course written in english.
* Every single "{" and "}" needs to start on a new line.
* Each keyword like "if", "else", "for" etc. needs to start on a new line. Except "else if (...)" which is allowed.
* Code must be easy readable.
* The code is compiled in Java 7, so use the Java 7 Coding Standards.

Example:<p>
Code like this:
```Java
} else {
```
is not allowed.
Correct is:
```Java
}
else
{
```

ToDo List
==============
- [x] Add config files for plugins (Done)
- [ ] Add translation option for plugins (by using the LanguageConfiguration Class) (SinkChat, SinkLibrary and SinkAntiSpam are done) 
