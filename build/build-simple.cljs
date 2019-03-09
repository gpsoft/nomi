(require '[lumo.build.api :as b])

(b/build
  "src"
  {:output-to "bmay.js"
   :optimizations :simple
   :target :nodejs})
