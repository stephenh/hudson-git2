
Install
=======

* Upload target/git.hpi to your instance of Hudson
* Choose Git as your SCM for your project

That's all I remember as I don't have a Hudson instance in front of me.

Building
========

You do try either:

1. Within the Hudson tree
  * The plugin originally expected to live in the `hudson/plugins/git2` directory of a Hudson svn checkout
  * While in the `git2` directory, just run `mvn`
2. Standalone tree
  * While in the `hudson-git2` directory, with no parent Hudson source, `mvn` might work
  * Note: you may have to move `dotm2_settings.xml` to `~/.m2/settings.xml`

Notes
=====

* The merge into other branches option is about the only code left over from the original git plugin and I haven't personally used it--it may or may not work

Acknowledgements
================

* The original Hudson git plugin author for providing a great place to start copy/pasting from

