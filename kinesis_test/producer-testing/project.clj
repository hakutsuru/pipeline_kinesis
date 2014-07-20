(defproject producer-testing "0.0.0"
  :description "test producer for kinesis"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [amazonica "0.2.19"]
                 [cheshire "5.3.1"]]
  :main ^:skip-aot producer-testing.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
