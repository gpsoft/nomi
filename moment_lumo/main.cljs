#!/usr/bin/env lumo

;; Play with repl:
;;   $ npm run repl

(require 'moment
         'fs
         '[clojure.pprint :as pp])

(defn pp-str
  [data]
  (with-out-str
    (pp/pprint data)))

(defn datetime
  []
  (-> (moment)
      (.format "YYYY-MM-DD hh:mm:ss")))

(defn read-json
  [fpath]
  (-> (fs/readFileSync fpath "utf8")
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(defn write-str
  [fpath s]
  (fs/writeFileSync fpath s))

#_(write-str
  "result.txt"
  (str
    (datetime)
    \newline
    (pp-str (read-json "package.json"))))
