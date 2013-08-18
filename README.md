uberrepl
============

Uberrepl to work with multiple, possible related projects in one repl.

Setup
=====

All you need to do is symlink those projects you want to work with in the checkouts directory. For example:
```sh
cd checkouts
ln -s ../sample_projects/workflow_demo_be
ln -s ../sample_projects/workflow_demo_fe
```

Prerequisites
=============

Strictly speaking there is not much. But uberrepl assumes a few things. First of all it assumes that your subprojects are all created using the same template to support repl driven development. Meaning you have a dev directory which is only on the classpath for the dev profile and that you are using Stuart Sierra's workflow more or less: see [his blogpost](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) and/or [Simon Katz's sample project](https://github.com/simon-katz/clojure-workflow-demo). It also assumes that you create a repl namespace in a dedicated directory under dev directory which your user namespace uses in your projects. To set up your projects properly is no magic, please see sample projects for details.

If your subprojects are set up properly but somewhat differently than the sample projects you might need to tweak the variables in dev/user.clj to point to the right directories in your subprojects. Same goes for the variables defining the prefixes for certain app manager functions in your subprojects.

Usage
=====

Start the repl for the uberrepl project.

If you are following Stuart Sierra's workflow you will get a repl which has all the code in it for the subprojects symlinked and all the project bootstrapping functions are also available in your uberrepl.
From that point use

```clojure
(uberrepl-reset)
```
to refresh all the changed source files into your repl. Then run
```clojure
(startup-all)
```
to start up your subprojects.

While hacking on those projects keep repeating the above two functions calls in your repl to quickly refresh and restart your applications.

Sample projects
===============

todo

Rationals
=========

todo

How it works
============

The uberrepl project file will lein install the subprojects symlinked. After that the user namespace will get loaded from the dev directory automatically. The uberrepl user namespace has some logic in its turn that finds and uses the subprojects' development application instance manager namespaces dynamically.

The uberrepl obviously exploits leiningen's checkout dependency feature and uses all the goodies in [tools.namespace](https://github.com/clojure/tools.namespace).

Possible problems
=================

- if you are using the same libraries/jars in your projects but different versions you might end up with some incompatibility problems
- if you use the project specific application manager functions in the uberrepl, specially the one which refreshes namespaces using tools.namespace you may end up in an inconsistent state in your uberrepl. That meanly happens if you change code in multiple subprojects but try to refresh/reset only one of them. Use uberrepl specific batch functions instead.
- if your application shuts down slowly or fails to do so and uses a port to communicate it might still running when the new instance tries to bind on the given port. that results in a error and a lost reference to the running application in the uberrepl. make sure that your application shuts down properly and reasonbly quickly or run
```clojure
(shutdown-all)
```
before running
```clojure
(uberrepl-reset)
```

Todos
=====

- create lein project template with appopriate files in dev directory (fork [reloaed](https://github.com/stuartsierra/reloaded))
