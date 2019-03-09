(ns bmay.core
  (:require moment
            fs
            [cljs.pprint :as pp]))

(defn- pp-str
  [data]
  (with-out-str
    (pp/pprint data)))  ;; this causes warning on build, why?

(defn- datetime
  []
  (-> (moment)
      (.format "YYYY-MM-DD hh:mm:ss")))

(defn- read-json
  [fpath]
  (-> (fs/readFileSync fpath "utf8")
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(defn- write-str
  [fpath s]
  (fs/writeFileSync fpath s))

(defn -main
  [& args]
  (let [fpath "result.txt"]
    (println "Writing to" fpath)
    (write-str
      fpath
      (str
        (datetime)
        \newline
        (pp-str (read-json "package.json"))))
    (println "Done")))

(set! *main-cli-fn* -main)
