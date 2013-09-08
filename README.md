# uberrepl

REPL to work with multiple, possible related projects in one repl.

## Setup

Uberrepl is not library. Rather, you clone this repository, point it to your subprojects.

To do this all you need to do is symlink those projects you want to work with in the checkouts directory. For example:
```sh
cd checkouts
ln -s ../sample_projects/workflow_demo_be
ln -s ../sample_projects/workflow_demo_fe
```
## Prerequisites

Strictly speaking there is not much. The idea of uberrepl (i.e. one repl to work with multiple projects) works no matter how the subprojects are set up. Also this project gives you a REPL with all the code and dependencies of the symlinked subprojects on its classpath. This is already a nice feature. However, uberrepl assumes that the subprojects are using a particular flavor of [Stuart Sierra's reloaded](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) workflow. See the sample projects for details or you can use [this](https://github.com/benedekfazekas/reloaded) leiningen template derived from Stuart Sierra's reloaded template to create your project initially. This template is uberrepl compliant out of the box.

The make an existing project reloaded workflow and uberrepl compliant is not difficult either. You basically have to do the following:
- create a dev directory in your project which is only on the classpath for the dev profile
- create an 'user' namespace in this dev directory
- this 'user' namespace delegates the releoaded workflow style application manager functions to a namespace in a subdirectory of the dev directory

If your projects already use the reloaded workflow all you need to do is move the app manager functions from the user namespace to a dedicated repl namespace in a subdirectory of dev directory.

If your subprojects are set up properly but somewhat differently than the sample projects you might need to tweak the variables in `uberrepl/dev/user.clj` to point to the right directories in your subprojects. Same goes for the variables defining the prefixes for certain app manager functions in your subprojects.

## Usage

Start the repl for the uberrepl project. If you are using emacs open uberrepl's ```project.clj``` and

   M-x nrepl-jack-in

(or use the appropriate keyboard short cut for jacking in).

Alternatively, if you are using command line, go to your project's root directory and type

   lein repl

If you are following Stuart Sierra's reloaded workflow you will get a repl which has all the code in it for the subprojects symlinked and all the project bootstrapping functions are also available in your uberrepl.
From that point use

```clojure
(uberrepl-reset)
```
to refresh all the changed source files into your repl. Then run
```clojure
(uberrepl-startup-all)
```
to start up your subprojects.

While hacking on those projects keep repeating the above two functions calls in your repl to quickly refresh and restart your applications.

## Sample projects

See sample_projects directory. To play with them symlink them in the checkouts directory (see above), and you are ready to go (see usage section).

## How it works

The uberrepl project file will lein install the subprojects symlinked. After that the user namespace will get loaded from the dev directory automatically. The uberrepl user namespace has some logic in its turn that finds and uses the subprojects' application instance manager namespaces dynamically.

The uberrepl obviously exploits leiningen's checkout dependency feature and uses all the goodies in [tools.namespace](https://github.com/clojure/tools.namespace).

## Possible problems

- if you are using the same libraries/jars in your projects but different versions you might end up with some incompatibility problems
- if you use the project specific application manager functions in the uberrepl, specially the one which refreshes namespaces using tools.namespace you may end up in an inconsistent state in your uberrepl. That meanly happens if you change code in multiple subprojects but try to refresh/reset only one of them. Use uberrepl specific batch functions instead.
- if your application shuts down slowly or fails to do so and uses a port to communicate it might still running when the new instance tries to bind on the given port. that results in a error and a lost reference to the running application in the uberrepl. make sure that your application shuts down properly and reasonbly quickly or run
```clojure
(uberrepl-shutdown-all)
```
before running
```clojure
(uberrepl-reset)
```

## Todos
