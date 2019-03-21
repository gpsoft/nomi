(in-ns 'user)

(require 'cljs.repl.node)

(cider.piggieback/cljs-repl
  (cljs.repl.node/repl-env))
