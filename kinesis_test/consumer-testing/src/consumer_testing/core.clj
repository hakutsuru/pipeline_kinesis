(ns consumer-testing.core
  (:require [amazonica.aws.kinesis :as kinesis])      
  (:gen-class))


;; set credentials to ensure correct endpoint
(def cred {:access-key (System/getenv "AWS_ACCESS_KEY_ID")
           :secret-key (System/getenv "AWS_SECRET_KEY")
           :endpoint   (or (System/getenv "AWS_PIPELINE_REGION") 
                            "us-west-2")})


;; set producer server and stream name
(def pipeline-name (or (System/getenv "AWS_PIPELINE_NAME") 
                        "kinesis-api-test"))


;; define shard-id and batch-limit
;; this is meant to be a simple test of a single shard stream
;; if there is any doubt about shard-id, check it via the
;; put-result from producer-testing 
(def shard-id "shardId-000000000000")
(def batch-limit 2)


;; obtain pipeline start position
(def cursor-start (kinesis/get-shard-iterator cred
                                              pipeline-name
                                              shard-id
                                              "TRIM_HORIZON"))

;; consume events from aws kinesis
;; check for existing streams promotes sanity and ensures
;; predictable output formatting (otherwise first call 
;; to aws may result in info item placed oddly)
(defn kinesis-consume
   []
   (def streams (:stream-names (kinesis/list-streams cred)))
   (println "")
   (println "hey now, let us consume!")
   (println "streams available:" streams "\n")
   (def shard-iterator cursor-start)
   ;; (println shard-iterator)
   ;; (println (kinesis/list-streams cred))
   ;; (println (kinesis/describe-stream cred pipeline-name))
   (loop [x 0]
     (when (< x 888)
       (def get-result
         (kinesis/get-records
           cred
           :shard-iterator shard-iterator
           :limit batch-limit))
       (def shard-iterator (:next-shard-iterator get-result))
       (def record-array (:records get-result))
       (doseq [log-event record-array]
         (println (:data log-event) "\n"))
       (Thread/sleep 400)
       (recur (+ x 1)))))


(defn -main
  [& args]
  (kinesis-consume))
