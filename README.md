# sally
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/clojurecup2014/sally?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Static Analysis Library and service yo!

Wolly Mare's clojure cup submission.

## Development

Start a REPL (in a terminal: `lein repl`, or from Emacs: open a
clj/cljs file in the project, then do `M-x cider-jack-in`. Make sure
CIDER is up to date).

In the REPL do

```clojure
(run)
(browser-repl)
```

The first starts the webserver at 10555. The second starts the Weasel
REPL server.

In a terminal do `lein figwheel`, this will watch and recompile your
ClojureScript, and start the figwheel server (the
default). Whenever your code changes, figwheel will recompile it and
send it to the browser immediately.

Now browse to `http://localhost:10555` and enjoy.
