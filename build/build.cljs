(require '[lumo.build.api :as b])

(b/build
  "src"
  {:main 'bmay.core
   :output-to "bmay.js"
   :optimizations :none
   :target :nodejs})
