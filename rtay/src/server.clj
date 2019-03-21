(in-ns 'user)

(require '[nrepl.server :as nrepl]
         '[cider.nrepl :as cider]
         '[cider.piggieback :as pback])

;; ■メモ
;; コンテナ側で:
;;    $ clj -Anrepl
;;    user=> (load "server")
;; ホスト側で:
;;    $ lein repl :connect 127.0.0.1:3575
;;    user=> (load "client")
;; cljs replがつながり、 (doc conj)や (.log js/console "Hey")できる。
;; でも、fireplace経由だとダメ。
;; :PigNode後、cppすると例外。

(defonce server
  (nrepl/start-server
    :bind "0.0.0.0"
    :port 3575
    :handler (nrepl/default-handler #'pback/wrap-cljs-repl)))

(let [port (:port server)]
  (spit ".nrepl-port" port)
  (println "nREPL server is running on port" port))
