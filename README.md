# uberrepl

REPL to work with multiple, possible related projects in one REPL.

## Setup

Uberrepl is not library. Rather, you clone this repository, and point it to your subprojects to get one REPL for all of them.

To do this all you need to do is symlink those projects you want to work with in the checkouts directory. For example:
```sh
cd checkouts
ln -s ../sample_projects/workflow_demo_be
ln -s ../sample_projects/workflow_demo_fe
```

## Usage

Start the REPL for the uberrepl project. If you are using emacs open uberrepl's ```project.clj``` and

    M-x nrepl-jack-in

(or use the appropriate keyboard short cut for jacking in).

Alternatively, if you are using command line, go to your project's root directory and type

    lein repl

You will get a REPL which has all the code in it for the subprojects symlinked. If you are following [Stuart Sierra's reloaded](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) workflow all the project bootstrapping functions are also available in your uberrepl.

From that point use

```clojure
(uberrepl-reset)
```
to refresh all the changed source files into your REPL. Then run
```clojure
(uberrepl-startup-all)
```
to start up your subprojects.

While hacking on those projects keep repeating the above two functions calls in your REPL to quickly refresh and restart your applications. Alternatively, if you use emacs, you can easily set up a keyboard shortcut to run these:

    (defun uberrepl-reset ()
      (interactive)
      (save-some-buffers)
      (set-buffer "*nrepl*")
      (goto-char (point-max))
      (insert "(user/uberrepl-reset)")
      (nrepl-return))

    (global-set-key (kbd "C-c r") 'uberrepl-reset)

    (defun uberrepl-start ()
      (interactive)
      (save-some-buffers)
      (set-buffer "*nrepl*")
      (goto-char (point-max))
      (insert "(user/uberrepl-startup-all)")
      (nrepl-return))

    (global-set-key (kbd "C-c s") 'uberrepl-start)

## Prerequisites

Strictly speaking there is not much. The idea of uberrepl (i.e. one REPL to work with multiple projects) works no matter how the subprojects are set up. Also this project gives you a REPL with all the code and dependencies of the symlinked subprojects on its classpath: a nice feature already. However, uberrepl assumes that the subprojects are using a particular flavor of [Stuart Sierra's reloaded](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) workflow. See the sample projects for details or you can use [this](https://github.com/benedekfazekas/reloaded) leiningen template derived from Stuart Sierra's reloaded template to create your project initially. This template is uberrepl compliant out of the box.

### Make existing project to uberrepl and reloaded workflow compliant

You basically have to do the following:
- create a dev directory in your project which is only on the classpath for the dev profile
- create an 'user' namespace in this dev directory
- this 'user' namespace delegates the releoaded workflow style application manager functions to a namespace in a subdirectory of the dev directory
- implement the project manager functions (see sample projects or [Simon Katz's sample project](https://github.com/simon-katz/clojure-workflow-demo) or the above blogpost by Stuart)

If your projects already use the reloaded workflow all you need to do is move the app manager functions from the user namespace to a dedicated REPL namespace in a subdirectory of dev directory.

You should end up with something like this:

    your_project
    ├── README.md
    ├── epl-v10.html
    ├── project.clj
    ├── dev
    │   └── user.clj
    │   └── project_repl
    │       └── your_project.clj
    └── src
        └── com
            └── example
                └── your_project.clj


If your subprojects are set up properly but somewhat differently than the sample projects you might need to tweak the variables in `uberrepl/dev/user.clj` to point to the right directories in your subprojects. Same goes for the variables defining the prefixes for certain app manager functions in your subprojects.

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
