(defproject consumer-testing "0.0.0"
  :description "test consumer for kinesis"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [amazonica "0.2.19"]]
  :main ^:skip-aot consumer-testing.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
