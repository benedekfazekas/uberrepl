uberrepl
============

Uberrepl to work with multiple, possible related projects in one repl.

Setup
=====

All you need to do is symlink those projects you want to work with in the checkouts directory. For example:
```sh
cd checkouts
ln -s ../../clj_fe
ln -s ../../clj_fe_adm
```

Usage
=====

Start the repl for the uberrepl project.

If you are following Stuart Sierra's workflow -- see [his blogpost](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) --, you will get a repl which has all the code for the subprojects symlinked and all the project bootstrapping functions are also available in your uberrepl: you may run them side by side while modifing their code using the same repl.


How it works
============

The uberrepl project file will lein install the subprojects you symlinked. After that the user namespace will get loaded from the dev directory automatically. The uberrepl user namespace has some logic in its turn that finds and loads the subprojects' user namespaces.

Possible problems
=================

- if you are using the same libraries/jars in your projects but different versions you might end up with some incompatibility problems

Todos
=====

- inspect project file in a proper way instead of regexp, use leiningen's project ns
- put subproject dev dependencies on the classpath as well -- currenlty if user ns depends on dev profile dependencies uberrepl will fail to startup the subproject
- inspecting user namespaces of subprojects: create functions to reset/restart all projects in uberrepl
- create test projects: one of them simon katz's clojure-workflow-demo, other one proxying it? or a msg driven app and modify workflow-demo to be read only?
