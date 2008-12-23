
Install
=======

* Upload target/git.hpi to your instance of Hudson
* Choose Git as your SCM for your project

That's all I remember as I don't have a Hudson instance in front of me.

Building
========

* This plugin expects to live in the `hudson/plugins/git2` directory of a Hudson svn checkout
* Then just run `mvn` from the `git2` directory

Notes
=====

* The merge into other branches option is about the only code left over from the original git plugin and I haven't personally used it--it may or may not work

Acknowledgements
================

* The original Hudson git plugin author for providing a great place to start copy/pasting from

