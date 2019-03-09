(require '[lumo.build.api :as b])

(b/build
  "src"
  {:output-to "out/rtay.js"
   :optimizations :simple
   :target :nodejs})
